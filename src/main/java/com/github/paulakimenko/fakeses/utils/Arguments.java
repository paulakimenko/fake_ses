package com.github.paulakimenko.fakeses.utils;

public final class Arguments {
    private static final String NAMESPACE = "fakeses";

    private final int port;
    private final int threadCount;
    private final String workDir;

    public static Arguments getFromEnviroment() {
        return new Arguments(
                Integer.parseInt(System.getProperty(NAMESPACE + ".port", "8000")),
                Integer.parseInt(System.getProperty(NAMESPACE + ".threadcount", "10")),
                System.getProperty(NAMESPACE + ".workdir", "./messages")
        );
    }

    private Arguments(int port, int threadCount, String workDir) {
        this.port = port;
        this.threadCount = threadCount;
        this.workDir = workDir;
    }

    public int getPort() {
        return port;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public String getWorkDir() {
        return workDir;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Arguments{");
        sb.append("port=").append(port);
        sb.append(", threadCount=").append(threadCount);
        sb.append(", workDir='").append(workDir).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
