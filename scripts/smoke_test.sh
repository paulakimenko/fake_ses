#!/usr/bin/env bash
set -euo pipefail

# Configurable via env vars
IMAGE="${IMAGE:-paulakimenko/fake_ses:0.3}"
PORT="${PORT:-8568}"
NAME="${NAME:-fake_ses_smoke}"
HOST="${HOST:-127.0.0.1}"

started_by_script=0

log() { echo "[smoke] $*"; }
die() { echo "[smoke] ERROR: $*" >&2; exit 1; }

cleanup() {
  if [[ "$started_by_script" == "1" ]]; then
    log "Stopping container $NAME"
    docker rm -f "$NAME" >/dev/null 2>&1 || true
  fi
}
trap cleanup EXIT

ensure_container() {
  if docker ps --format '{{.Names}}' | grep -q "^${NAME}$"; then
    log "Container $NAME already running"
    return
  fi
  if docker ps -a --format '{{.Names}}' | grep -q "^${NAME}$"; then
    log "Removing exited container $NAME"
    docker rm -f "$NAME" >/dev/null
  fi
  log "Starting container $NAME from $IMAGE on port $PORT"
  docker run -d --name "$NAME" -p "$PORT:8567" "$IMAGE" >/dev/null
  started_by_script=1
  # Wait briefly for server to boot
  sleep 1
}

http_code() {
  curl -s -o /dev/null -w "%{http_code}\n" "http://${HOST}:${PORT}$1"
}

get_json() {
  curl -s "http://${HOST}:${PORT}$1"
}

post_form() {
  curl -s -X POST "http://${HOST}:${PORT}/" \
    --data-urlencode "Action=SendEmail" \
    --data-urlencode "Message.Subject.Data=${1}" \
    --data-urlencode "Source=sender@example.com" \
    --data-urlencode "Destination.ToAddresses.member.1=to1@example.com" \
    --data-urlencode "Destination.ToAddresses.member.2=to2@example.com" \
    --data-urlencode "ReplyToAddresses.member.1=reply@example.com" \
    --data-urlencode "Message.Body.Text.Data=hi" \
    --data-urlencode "Message.Body.Html.Data=<b>hi</b>"
}

main() {
  ensure_container

  log "Health check"
  code=$(http_code "/api/messages")
  [[ "$code" == "200" ]] || die "/api/messages returned $code"

  log "Create message"
  subject="SmokeTest-$(date +%s)"
  xml=$(post_form "$subject")
  echo "$xml" | grep -q "<MessageId>" || die "POST / did not return MessageId XML"

  log "Filter by subject"
  filtered=$(get_json "/api/messages?filter=subject:${subject}")
  echo "$filtered" | grep -q '"subject":"' || die "Filtered messages missing subject"

  log "Delete all"
  code=$(curl -s -o /dev/null -w "%{http_code}\n" -X DELETE "http://${HOST}:${PORT}/api/messages")
  [[ "$code" == "204" ]] || die "DELETE /api/messages returned $code"

  log "Verify empty"
  empty=$(get_json "/api/messages")
  echo "$empty" | grep -q '"data":\[\]' || die "Expected empty data array, got: $empty"

  log "PASS"
}

main "$@"
