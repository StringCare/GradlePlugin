class ClockT {
    long startTimeInMs

    public ClockT() {
        this(System.currentTimeMillis())
    }

    public ClockT(long startTimeInMs) {
        this.startTimeInMs = startTimeInMs
    }

    long getTimeInMs() {
        return System.currentTimeMillis() - startTimeInMs
    }
}
