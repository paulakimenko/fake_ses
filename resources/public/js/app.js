$(document).ready(function () {
  var MESSAGES_API_URL = '/api/messages';

  function findOne(array, predicate) {
    var filtered = array.filter(predicate);
    if (filtered.length === 0) {
      throw new Error('No one found');
    } else if (filtered.length > 1) {
      throw new Error('More than one found');
    } else {
      return filtered[0];
    }
  }

  function unixtimeToStr (unixtime) {
    return new Date(unixtime * 1000).toISOString().split('.')[0].split('-').join('.').replace('T', ' ');
  }

  function shortAddressesStr (addresses) {
    var result = '';
    if (addresses.length == 0) return result;
    result += addresses[0];
    if (addresses.length > 1) result += ' ...';
    return result;
  }

  var SesMockAPI = function (defErrorHandler) {
    this.loadAllMessages = function (success, fail) {
      $.ajax({
        url: MESSAGES_API_URL,
        method: 'GET',
        dataType: 'json'
      })
        .done(success)
        .fail(fail || defErrorHandler);
    };

    this.deleteAllMessages = function (success, fail) {
      $.ajax({
        url: MESSAGES_API_URL,
        method: 'DELETE',
        dataType: 'json'
      }).done(success)
        .fail(fail || defErrorHandler);
    };
  };

  var errorHandler = function (jqXHR) {
    alert('error');
  };
  
  var api = new SesMockAPI(errorHandler);

  var showMessagePopup = function (message) {
    var $popup = $('.popup');
    var $details = $('.message-base-details');
    var $contentPanel = $details.find('.message-content');

    var baseDetails = {
      'subject': message['subject'],
      'received': unixtimeToStr(message['date_received']),
      'ses-action': message['action'],
      'sender': message['source'],
      'destination': message['destination'].join(', '),
      'reply-to': message['reply_to_addresses'].join(', ')
    };

    var contentParts = {
      'text-content': { title: 'Open Text Content', data: message['text_content'].split('\n').join('<br>') },
      'html-content': { title: 'Open HTML Content', data: message['html_content'] },
      'raw-content': { title: 'Open Raw Content', data: message['raw_content'] }
    };

    $contentPanel.empty();

    $.each(baseDetails, function (className, data) {
      $details.find('.' + className + ' .value').text(data);
    });

    $.each(contentParts, function (className, details) {
      if (details.data) {
        $contentPanel.append(
          '<div class="col-4">' +
          '<button class="btn ' + className + '">' + details.title + '</button>' +
          '</div>'
        );

        $contentPanel.find('.' + className).click(function () {
          var newWindow = window.open();
          $(newWindow.document.body).html(details.data);
        })
      }
    });

    $popup.find('.close').click(function () {
      $popup.css('display', 'none');
    });

    $popup.css('display', 'block');
  };
  
  var renderMessages = function () {
    api.loadAllMessages(function (respData) {
      var messages = respData['data'];
      var messagesStr = '';

      messages.forEach(function (message) {
        var messageRowData = [
          message['subject'],
          message['source'],
          shortAddressesStr(message['destination']),
          unixtimeToStr(message['date_received'])
        ];
        messagesStr += '<div class="row message-row" data-message-id="' + message['id'] + '">';
        messageRowData.forEach(function (value) {
          return messagesStr += '<div class="col-3">' + value + '</div>';
        });
        messagesStr += '</div>';
      });

      $('.message-rows').empty().append(messagesStr);

      $('.message-row').click(function () {
        var messageId = $(this).attr('data-message-id');
        showMessagePopup(findOne(messages, function (messageEl) {
          return messageEl['id'] === messageId;
        }));
      });
    });
  };

  var setOnDeleteAll = function () {
    $('.delete-all').click(function () {
      api.deleteAllMessages(function () {
        renderMessages();
      });
    });
  };

  renderMessages();
  setOnDeleteAll();
});