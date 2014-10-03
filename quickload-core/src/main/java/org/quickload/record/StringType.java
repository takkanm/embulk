package org.quickload.record;

public class StringType
        extends AbstractType
{
    public static final StringType STRING = new StringType();

    private StringType()
    {
    }

    public String getName()
    {
        return "string";
    }

    @Override
    public Class<?> getJavaType()
    {
        return String.class;
    }

    @Override
    public byte getFixedStorageSize()
    {
        return Type.VARIABLE_LENGTH_INDEX_SIZE;
    }

    static String getStringValue(RecordCursor cursor, int columnIndex)
    {
        int index = cursor.getVariableLengthIndex(columnIndex);
        return cursor.getPage().getStringReference(index);
    }

    static void setStringValue(RecordBuilder builder, int columnIndex, String value)
    {
        int index = builder.getPage().addStringReference(value);
        builder.setVariableLengthIndex(columnIndex, index);
    }

    @Override
    public String getString(RecordCursor cursor, int columnIndex)
    {
        return getStringValue(cursor, columnIndex);
    }

    @Override
    public void setString(RecordBuilder builder, int columnIndex, String value)
    {
        setStringValue(builder, columnIndex, value);
    }

    @Override
    public void consume(RecordCursor cursor, RecordConsumer consumer, Column column)
    {
        if (cursor.isNull(column.getIndex())) {
            consumer.setNull(column);
        } else {
            consumer.setString(column, getStringValue(cursor, column.getIndex()));
        }
    }

    @Override
    public void produce(RecordBuilder builder, RecordProducer producer, Column column)
    {
        producer.setString(column, new Setter(builder, column.getIndex()));
    }

    public static class Setter
    {
        private final RecordBuilder builder;
        private final int columnIndex;

        Setter(RecordBuilder builder, int columnIndex)
        {
            this.builder = builder;
            this.columnIndex = columnIndex;
        }

        public void setNull()
        {
            builder.setNull(columnIndex);
        }

        public void setString(String value)
        {
            setStringValue(builder, columnIndex, value);
        }
    }
}