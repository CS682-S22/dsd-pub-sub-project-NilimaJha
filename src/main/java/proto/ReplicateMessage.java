// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ReplicateMessage.proto

package proto;

public final class ReplicateMessage {
  private ReplicateMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ReplicateMessageDetailsOrBuilder extends
      // @@protoc_insertion_point(interface_extends:tutorial.ReplicateMessageDetails)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>bool synchronous = 1;</code>
     * @return The synchronous.
     */
    boolean getSynchronous();

    /**
     * <code>uint64 messageId = 2;</code>
     * @return The messageId.
     */
    long getMessageId();

    /**
     * <code>string topic = 3;</code>
     * @return The topic.
     */
    java.lang.String getTopic();
    /**
     * <code>string topic = 3;</code>
     * @return The bytes for topic.
     */
    com.google.protobuf.ByteString
        getTopicBytes();

    /**
     * <code>bytes message = 4;</code>
     * @return The message.
     */
    com.google.protobuf.ByteString getMessage();

    /**
     * <code>uint64 totalMessage = 5;</code>
     * @return The totalMessage.
     */
    long getTotalMessage();

    /**
     * <code>repeated bytes messageBatch = 6;</code>
     * @return A list containing the messageBatch.
     */
    java.util.List<com.google.protobuf.ByteString> getMessageBatchList();
    /**
     * <code>repeated bytes messageBatch = 6;</code>
     * @return The count of messageBatch.
     */
    int getMessageBatchCount();
    /**
     * <code>repeated bytes messageBatch = 6;</code>
     * @param index The index of the element to return.
     * @return The messageBatch at the given index.
     */
    com.google.protobuf.ByteString getMessageBatch(int index);
  }
  /**
   * Protobuf type {@code tutorial.ReplicateMessageDetails}
   */
  public static final class ReplicateMessageDetails extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:tutorial.ReplicateMessageDetails)
      ReplicateMessageDetailsOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use ReplicateMessageDetails.newBuilder() to construct.
    private ReplicateMessageDetails(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ReplicateMessageDetails() {
      topic_ = "";
      message_ = com.google.protobuf.ByteString.EMPTY;
      messageBatch_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new ReplicateMessageDetails();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private ReplicateMessageDetails(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {

              synchronous_ = input.readBool();
              break;
            }
            case 16: {

              messageId_ = input.readUInt64();
              break;
            }
            case 26: {
              java.lang.String s = input.readStringRequireUtf8();

              topic_ = s;
              break;
            }
            case 34: {

              message_ = input.readBytes();
              break;
            }
            case 40: {

              totalMessage_ = input.readUInt64();
              break;
            }
            case 50: {
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                messageBatch_ = new java.util.ArrayList<com.google.protobuf.ByteString>();
                mutable_bitField0_ |= 0x00000001;
              }
              messageBatch_.add(input.readBytes());
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000001) != 0)) {
          messageBatch_ = java.util.Collections.unmodifiableList(messageBatch_); // C
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return proto.ReplicateMessage.internal_static_tutorial_ReplicateMessageDetails_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.ReplicateMessage.internal_static_tutorial_ReplicateMessageDetails_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.ReplicateMessage.ReplicateMessageDetails.class, proto.ReplicateMessage.ReplicateMessageDetails.Builder.class);
    }

    public static final int SYNCHRONOUS_FIELD_NUMBER = 1;
    private boolean synchronous_;
    /**
     * <code>bool synchronous = 1;</code>
     * @return The synchronous.
     */
    @java.lang.Override
    public boolean getSynchronous() {
      return synchronous_;
    }

    public static final int MESSAGEID_FIELD_NUMBER = 2;
    private long messageId_;
    /**
     * <code>uint64 messageId = 2;</code>
     * @return The messageId.
     */
    @java.lang.Override
    public long getMessageId() {
      return messageId_;
    }

    public static final int TOPIC_FIELD_NUMBER = 3;
    private volatile java.lang.Object topic_;
    /**
     * <code>string topic = 3;</code>
     * @return The topic.
     */
    @java.lang.Override
    public java.lang.String getTopic() {
      java.lang.Object ref = topic_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        topic_ = s;
        return s;
      }
    }
    /**
     * <code>string topic = 3;</code>
     * @return The bytes for topic.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getTopicBytes() {
      java.lang.Object ref = topic_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        topic_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int MESSAGE_FIELD_NUMBER = 4;
    private com.google.protobuf.ByteString message_;
    /**
     * <code>bytes message = 4;</code>
     * @return The message.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getMessage() {
      return message_;
    }

    public static final int TOTALMESSAGE_FIELD_NUMBER = 5;
    private long totalMessage_;
    /**
     * <code>uint64 totalMessage = 5;</code>
     * @return The totalMessage.
     */
    @java.lang.Override
    public long getTotalMessage() {
      return totalMessage_;
    }

    public static final int MESSAGEBATCH_FIELD_NUMBER = 6;
    private java.util.List<com.google.protobuf.ByteString> messageBatch_;
    /**
     * <code>repeated bytes messageBatch = 6;</code>
     * @return A list containing the messageBatch.
     */
    @java.lang.Override
    public java.util.List<com.google.protobuf.ByteString>
        getMessageBatchList() {
      return messageBatch_;
    }
    /**
     * <code>repeated bytes messageBatch = 6;</code>
     * @return The count of messageBatch.
     */
    public int getMessageBatchCount() {
      return messageBatch_.size();
    }
    /**
     * <code>repeated bytes messageBatch = 6;</code>
     * @param index The index of the element to return.
     * @return The messageBatch at the given index.
     */
    public com.google.protobuf.ByteString getMessageBatch(int index) {
      return messageBatch_.get(index);
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (synchronous_ != false) {
        output.writeBool(1, synchronous_);
      }
      if (messageId_ != 0L) {
        output.writeUInt64(2, messageId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(topic_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, topic_);
      }
      if (!message_.isEmpty()) {
        output.writeBytes(4, message_);
      }
      if (totalMessage_ != 0L) {
        output.writeUInt64(5, totalMessage_);
      }
      for (int i = 0; i < messageBatch_.size(); i++) {
        output.writeBytes(6, messageBatch_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (synchronous_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(1, synchronous_);
      }
      if (messageId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(2, messageId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(topic_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, topic_);
      }
      if (!message_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(4, message_);
      }
      if (totalMessage_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(5, totalMessage_);
      }
      {
        int dataSize = 0;
        for (int i = 0; i < messageBatch_.size(); i++) {
          dataSize += com.google.protobuf.CodedOutputStream
            .computeBytesSizeNoTag(messageBatch_.get(i));
        }
        size += dataSize;
        size += 1 * getMessageBatchList().size();
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof proto.ReplicateMessage.ReplicateMessageDetails)) {
        return super.equals(obj);
      }
      proto.ReplicateMessage.ReplicateMessageDetails other = (proto.ReplicateMessage.ReplicateMessageDetails) obj;

      if (getSynchronous()
          != other.getSynchronous()) return false;
      if (getMessageId()
          != other.getMessageId()) return false;
      if (!getTopic()
          .equals(other.getTopic())) return false;
      if (!getMessage()
          .equals(other.getMessage())) return false;
      if (getTotalMessage()
          != other.getTotalMessage()) return false;
      if (!getMessageBatchList()
          .equals(other.getMessageBatchList())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + SYNCHRONOUS_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getSynchronous());
      hash = (37 * hash) + MESSAGEID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getMessageId());
      hash = (37 * hash) + TOPIC_FIELD_NUMBER;
      hash = (53 * hash) + getTopic().hashCode();
      hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
      hash = (53 * hash) + getMessage().hashCode();
      hash = (37 * hash) + TOTALMESSAGE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getTotalMessage());
      if (getMessageBatchCount() > 0) {
        hash = (37 * hash) + MESSAGEBATCH_FIELD_NUMBER;
        hash = (53 * hash) + getMessageBatchList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.ReplicateMessage.ReplicateMessageDetails parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(proto.ReplicateMessage.ReplicateMessageDetails prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code tutorial.ReplicateMessageDetails}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:tutorial.ReplicateMessageDetails)
        proto.ReplicateMessage.ReplicateMessageDetailsOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return proto.ReplicateMessage.internal_static_tutorial_ReplicateMessageDetails_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return proto.ReplicateMessage.internal_static_tutorial_ReplicateMessageDetails_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                proto.ReplicateMessage.ReplicateMessageDetails.class, proto.ReplicateMessage.ReplicateMessageDetails.Builder.class);
      }

      // Construct using proto.ReplicateMessage.ReplicateMessageDetails.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        synchronous_ = false;

        messageId_ = 0L;

        topic_ = "";

        message_ = com.google.protobuf.ByteString.EMPTY;

        totalMessage_ = 0L;

        messageBatch_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return proto.ReplicateMessage.internal_static_tutorial_ReplicateMessageDetails_descriptor;
      }

      @java.lang.Override
      public proto.ReplicateMessage.ReplicateMessageDetails getDefaultInstanceForType() {
        return proto.ReplicateMessage.ReplicateMessageDetails.getDefaultInstance();
      }

      @java.lang.Override
      public proto.ReplicateMessage.ReplicateMessageDetails build() {
        proto.ReplicateMessage.ReplicateMessageDetails result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public proto.ReplicateMessage.ReplicateMessageDetails buildPartial() {
        proto.ReplicateMessage.ReplicateMessageDetails result = new proto.ReplicateMessage.ReplicateMessageDetails(this);
        int from_bitField0_ = bitField0_;
        result.synchronous_ = synchronous_;
        result.messageId_ = messageId_;
        result.topic_ = topic_;
        result.message_ = message_;
        result.totalMessage_ = totalMessage_;
        if (((bitField0_ & 0x00000001) != 0)) {
          messageBatch_ = java.util.Collections.unmodifiableList(messageBatch_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.messageBatch_ = messageBatch_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof proto.ReplicateMessage.ReplicateMessageDetails) {
          return mergeFrom((proto.ReplicateMessage.ReplicateMessageDetails)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(proto.ReplicateMessage.ReplicateMessageDetails other) {
        if (other == proto.ReplicateMessage.ReplicateMessageDetails.getDefaultInstance()) return this;
        if (other.getSynchronous() != false) {
          setSynchronous(other.getSynchronous());
        }
        if (other.getMessageId() != 0L) {
          setMessageId(other.getMessageId());
        }
        if (!other.getTopic().isEmpty()) {
          topic_ = other.topic_;
          onChanged();
        }
        if (other.getMessage() != com.google.protobuf.ByteString.EMPTY) {
          setMessage(other.getMessage());
        }
        if (other.getTotalMessage() != 0L) {
          setTotalMessage(other.getTotalMessage());
        }
        if (!other.messageBatch_.isEmpty()) {
          if (messageBatch_.isEmpty()) {
            messageBatch_ = other.messageBatch_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureMessageBatchIsMutable();
            messageBatch_.addAll(other.messageBatch_);
          }
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        proto.ReplicateMessage.ReplicateMessageDetails parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (proto.ReplicateMessage.ReplicateMessageDetails) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private boolean synchronous_ ;
      /**
       * <code>bool synchronous = 1;</code>
       * @return The synchronous.
       */
      @java.lang.Override
      public boolean getSynchronous() {
        return synchronous_;
      }
      /**
       * <code>bool synchronous = 1;</code>
       * @param value The synchronous to set.
       * @return This builder for chaining.
       */
      public Builder setSynchronous(boolean value) {
        
        synchronous_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bool synchronous = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearSynchronous() {
        
        synchronous_ = false;
        onChanged();
        return this;
      }

      private long messageId_ ;
      /**
       * <code>uint64 messageId = 2;</code>
       * @return The messageId.
       */
      @java.lang.Override
      public long getMessageId() {
        return messageId_;
      }
      /**
       * <code>uint64 messageId = 2;</code>
       * @param value The messageId to set.
       * @return This builder for chaining.
       */
      public Builder setMessageId(long value) {
        
        messageId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint64 messageId = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessageId() {
        
        messageId_ = 0L;
        onChanged();
        return this;
      }

      private java.lang.Object topic_ = "";
      /**
       * <code>string topic = 3;</code>
       * @return The topic.
       */
      public java.lang.String getTopic() {
        java.lang.Object ref = topic_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          topic_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string topic = 3;</code>
       * @return The bytes for topic.
       */
      public com.google.protobuf.ByteString
          getTopicBytes() {
        java.lang.Object ref = topic_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          topic_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string topic = 3;</code>
       * @param value The topic to set.
       * @return This builder for chaining.
       */
      public Builder setTopic(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        topic_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string topic = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearTopic() {
        
        topic_ = getDefaultInstance().getTopic();
        onChanged();
        return this;
      }
      /**
       * <code>string topic = 3;</code>
       * @param value The bytes for topic to set.
       * @return This builder for chaining.
       */
      public Builder setTopicBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        topic_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString message_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes message = 4;</code>
       * @return The message.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString getMessage() {
        return message_;
      }
      /**
       * <code>bytes message = 4;</code>
       * @param value The message to set.
       * @return This builder for chaining.
       */
      public Builder setMessage(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        message_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes message = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessage() {
        
        message_ = getDefaultInstance().getMessage();
        onChanged();
        return this;
      }

      private long totalMessage_ ;
      /**
       * <code>uint64 totalMessage = 5;</code>
       * @return The totalMessage.
       */
      @java.lang.Override
      public long getTotalMessage() {
        return totalMessage_;
      }
      /**
       * <code>uint64 totalMessage = 5;</code>
       * @param value The totalMessage to set.
       * @return This builder for chaining.
       */
      public Builder setTotalMessage(long value) {
        
        totalMessage_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint64 totalMessage = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearTotalMessage() {
        
        totalMessage_ = 0L;
        onChanged();
        return this;
      }

      private java.util.List<com.google.protobuf.ByteString> messageBatch_ = java.util.Collections.emptyList();
      private void ensureMessageBatchIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          messageBatch_ = new java.util.ArrayList<com.google.protobuf.ByteString>(messageBatch_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated bytes messageBatch = 6;</code>
       * @return A list containing the messageBatch.
       */
      public java.util.List<com.google.protobuf.ByteString>
          getMessageBatchList() {
        return ((bitField0_ & 0x00000001) != 0) ?
                 java.util.Collections.unmodifiableList(messageBatch_) : messageBatch_;
      }
      /**
       * <code>repeated bytes messageBatch = 6;</code>
       * @return The count of messageBatch.
       */
      public int getMessageBatchCount() {
        return messageBatch_.size();
      }
      /**
       * <code>repeated bytes messageBatch = 6;</code>
       * @param index The index of the element to return.
       * @return The messageBatch at the given index.
       */
      public com.google.protobuf.ByteString getMessageBatch(int index) {
        return messageBatch_.get(index);
      }
      /**
       * <code>repeated bytes messageBatch = 6;</code>
       * @param index The index to set the value at.
       * @param value The messageBatch to set.
       * @return This builder for chaining.
       */
      public Builder setMessageBatch(
          int index, com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureMessageBatchIsMutable();
        messageBatch_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes messageBatch = 6;</code>
       * @param value The messageBatch to add.
       * @return This builder for chaining.
       */
      public Builder addMessageBatch(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureMessageBatchIsMutable();
        messageBatch_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes messageBatch = 6;</code>
       * @param values The messageBatch to add.
       * @return This builder for chaining.
       */
      public Builder addAllMessageBatch(
          java.lang.Iterable<? extends com.google.protobuf.ByteString> values) {
        ensureMessageBatchIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, messageBatch_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes messageBatch = 6;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessageBatch() {
        messageBatch_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:tutorial.ReplicateMessageDetails)
    }

    // @@protoc_insertion_point(class_scope:tutorial.ReplicateMessageDetails)
    private static final proto.ReplicateMessage.ReplicateMessageDetails DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new proto.ReplicateMessage.ReplicateMessageDetails();
    }

    public static proto.ReplicateMessage.ReplicateMessageDetails getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ReplicateMessageDetails>
        PARSER = new com.google.protobuf.AbstractParser<ReplicateMessageDetails>() {
      @java.lang.Override
      public ReplicateMessageDetails parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ReplicateMessageDetails(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ReplicateMessageDetails> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ReplicateMessageDetails> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public proto.ReplicateMessage.ReplicateMessageDetails getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tutorial_ReplicateMessageDetails_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tutorial_ReplicateMessageDetails_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026ReplicateMessage.proto\022\010tutorial\"\215\001\n\027R" +
      "eplicateMessageDetails\022\023\n\013synchronous\030\001 " +
      "\001(\010\022\021\n\tmessageId\030\002 \001(\004\022\r\n\005topic\030\003 \001(\t\022\017\n" +
      "\007message\030\004 \001(\014\022\024\n\014totalMessage\030\005 \001(\004\022\024\n\014" +
      "messageBatch\030\006 \003(\014B\031\n\005protoB\020ReplicateMe" +
      "ssageb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_tutorial_ReplicateMessageDetails_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tutorial_ReplicateMessageDetails_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tutorial_ReplicateMessageDetails_descriptor,
        new java.lang.String[] { "Synchronous", "MessageId", "Topic", "Message", "TotalMessage", "MessageBatch", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
