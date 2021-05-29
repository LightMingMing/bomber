package com.bomber.function;

/**
 * 定长字符串
 *
 * @author MingMing Zhao
 */
@Group(Type.BASE)
@FuncInfo(requiredArgs = "length", optionalArgs = "prefix, suffix")
public class FixedLengthString implements Producer<String> {

    protected static final int MAX_LENGTH = 100;

    protected static final int MAX_COUNT_LENGTH = 18;

    private final int length;

    private final String prefix;

    private final String suffix;

    private final int numberLength;

    private final long mod;

    private long count;

    public FixedLengthString(int length) {
        this(length, "");
    }

    public FixedLengthString(int length, String prefix) {
        this(length, prefix, "");
    }

    public FixedLengthString(int length, String prefix, String suffix) {
        if (length < 1 || length > MAX_LENGTH) {
            throw new IllegalArgumentException("length: " + length + ", (expected: 1-" + MAX_LENGTH + ")");
        }
        this.length = length;

        this.prefix = prefix;
        this.suffix = suffix;

        int numberLength = length;
        if (prefix != null) {
            numberLength -= prefix.length();
        }
        if (suffix != null) {
            numberLength -= suffix.length();
        }
        if (numberLength < 1) {
            throw new IllegalArgumentException("numberLength: " + numberLength + ", (expected: > 0)");
        }
        this.numberLength = numberLength;
        this.mod = (long) Math.pow(10, Math.min(this.numberLength, MAX_COUNT_LENGTH));
    }

    private static int stringSize(long x) {
        long p = 10;
        for (int i = 1; i < MAX_COUNT_LENGTH; i++) {
            if (x < p)
                return i;
            p = 10 * p;
        }
        return MAX_COUNT_LENGTH;
    }

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder(length);

        if (prefix != null)
            sb.append(prefix);

        int paddedZeroLength = numberLength - stringSize(count);
        if (paddedZeroLength > 0) {
            sb.append("0".repeat(paddedZeroLength));
        }

        sb.append(count);

        if (suffix != null)
            sb.append(suffix);

        count = (++count) % mod;
        return sb.toString();
    }

    @Override
    public void jump(int steps) {
        count = (count + steps) % mod;
    }
}
