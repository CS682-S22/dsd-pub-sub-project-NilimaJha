// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: MessageFromBroker.proto

package proto;

public final class MessageFromBroker {
  private MessageFromBroker() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface MessageFromBrokerDetailsOrBuilder extends
      // @@protoc_insertion_point(interface_extends:tutorial.MessageFromBrokerDetails)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string type = 1;</code>
     * @return The type.
     */
    java.lang.String getType();
    /**
     * <code>string type = 1;</code>
     * @return The bytes for type.
     */
    com.google.protobuf.ByteString
        getTypeBytes();

    /**
     * <code>string topic = 2;</code>
     * @return The topic.
     */
    java.lang.String getTopic();
    /**
     * <code>string topic = 2;</code>
     * @return The bytes for topic.
     */
    com.google.protobuf.ByteString
        getTopicBytes();

    /**
     * <code>uint64 totalMessage = 3;</code>
     * @return The totalMessage.
     */
    long getTotalMessage();

    /**
     * <code>repeated bytes actualMessage = 4;</code>
     * @return A list containing the actualMessage.
     */
    java.util.List<com.google.protobuf.ByteString> getActualMessageList();
    /**
     * <code>repeated bytes actualMessage = 4;</code>
     * @return The count of actualMessage.
     */
    int getActualMessageCount();
    /**
     * <code>repeated bytes actualMessage = 4;</code>
     * @param index The index of the element to return.
     * @return The actualMessage at the given index.
     */
    com.google.protobuf.ByteString getActualMessage(int index);
  }
  /**
   * Protobuf type {@code tutorial.MessageFromBrokerDetails}
   */
  public static final class MessageFromBrokerDetails extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:tutorial.MessageFromBrokerDetails)
      MessageFromBrokerDetailsOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use MessageFromBrokerDetails.newBuilder() to construct.
    private MessageFromBrokerDetails(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private MessageFromBrokerDetails() {
      type_ = "";
      topic_ = "";
      actualMessage_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new MessageFromBrokerDetails();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private MessageFromBrokerDetails(
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
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              type_ = s;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              topic_ = s;
              break;
            }
            case 24: {

              totalMessage_ = input.readUInt64();
              break;
            }
            case 34: {
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                actualMessage_ = new java.util.ArrayList<com.google.protobuf.ByteString>();
                mutable_bitField0_ |= 0x00000001;
              }
              actualMessage_.add(input.readBytes());
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
          actualMessage_ = java.util.Collections.unmodifiableList(actualMessage_); // C
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return proto.MessageFromBroker.internal_static_tutorial_MessageFromBrokerDetails_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.MessageFromBroker.internal_static_tutorial_MessageFromBrokerDetails_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.MessageFromBroker.MessageFromBrokerDetails.class, proto.MessageFromBroker.MessageFromBrokerDetails.Builder.class);
    }

    public static final int TYPE_FIELD_NUMBER = 1;
    private volatile java.lang.Object type_;
    /**
     * <code>string type = 1;</code>
     * @return The type.
     */
    @java.lang.Override
    public java.lang.String getType() {
      java.lang.Object ref = type_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        type_ = s;
        return s;
      }
    }
    /**
     * <code>string type = 1;</code>
     * @return The bytes for type.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getTypeBytes() {
      java.lang.Object ref = type_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        type_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TOPIC_FIELD_NUMBER = 2;
    private volatile java.lang.Object topic_;
    /**
     * <code>string topic = 2;</code>
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
     * <code>string topic = 2;</code>
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

    public static final int TOTALMESSAGE_FIELD_NUMBER = 3;
    private long totalMessage_;
    /**
     * <code>uint64 totalMessage = 3;</code>
     * @return The totalMessage.
     */
    @java.lang.Override
    public long getTotalMessage() {
      return totalMessage_;
    }

    public static final int ACTUALMESSAGE_FIELD_NUMBER = 4;
    private java.util.List<com.google.protobuf.ByteString> actualMessage_;
    /**
     * <code>repeated bytes actualMessage = 4;</code>
     * @return A list containing the actualMessage.
     */
    @java.lang.Override
    public java.util.List<com.google.protobuf.ByteString>
        getActualMessageList() {
      return actualMessage_;
    }
    /**
     * <code>repeated bytes actualMessage = 4;</code>
     * @return The count of actualMessage.
     */
    public int getActualMessageCount() {
      return actualMessage_.size();
    }
    /**
     * <code>repeated bytes actualMessage = 4;</code>
     * @param index The index of the element to return.
     * @return The actualMessage at the given index.
     */
    public com.google.protobuf.ByteString getActualMessage(int index) {
      return actualMessage_.get(index);
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
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(type_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, type_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(topic_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, topic_);
      }
      if (totalMessage_ != 0L) {
        output.writeUInt64(3, totalMessage_);
      }
      for (int i = 0; i < actualMessage_.size(); i++) {
        output.writeBytes(4, actualMessage_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(type_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, type_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(topic_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, topic_);
      }
      if (totalMessage_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(3, totalMessage_);
      }
      {
        int dataSize = 0;
        for (int i = 0; i < actualMessage_.size(); i++) {
          dataSize += com.google.protobuf.CodedOutputStream
            .computeBytesSizeNoTag(actualMessage_.get(i));
        }
        size += dataSize;
        size += 1 * getActualMessageList().size();
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
      if (!(obj instanceof proto.MessageFromBroker.MessageFromBrokerDetails)) {
        return super.equals(obj);
      }
      proto.MessageFromBroker.MessageFromBrokerDetails other = (proto.MessageFromBroker.MessageFromBrokerDetails) obj;

      if (!getType()
          .equals(other.getType())) return false;
      if (!getTopic()
          .equals(other.getTopic())) return false;
      if (getTotalMessage()
          != other.getTotalMessage()) return false;
      if (!getActualMessageList()
          .equals(other.getActualMessageList())) return false;
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
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getType().hashCode();
      hash = (37 * hash) + TOPIC_FIELD_NUMBER;
      hash = (53 * hash) + getTopic().hashCode();
      hash = (37 * hash) + TOTALMESSAGE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getTotalMessage());
      if (getActualMessageCount() > 0) {
        hash = (37 * hash) + ACTUALMESSAGE_FIELD_NUMBER;
        hash = (53 * hash) + getActualMessageList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.MessageFromBroker.MessageFromBrokerDetails parseFrom(
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
    public static Builder newBuilder(proto.MessageFromBroker.MessageFromBrokerDetails prototype) {
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
     * Protobuf type {@code tutorial.MessageFromBrokerDetails}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:tutorial.MessageFromBrokerDetails)
        proto.MessageFromBroker.MessageFromBrokerDetailsOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return proto.MessageFromBroker.internal_static_tutorial_MessageFromBrokerDetails_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return proto.MessageFromBroker.internal_static_tutorial_MessageFromBrokerDetails_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                proto.MessageFromBroker.MessageFromBrokerDetails.class, proto.MessageFromBroker.MessageFromBrokerDetails.Builder.class);
      }

      // Construct using proto.MessageFromBroker.MessageFromBrokerDetails.newBuilder()
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
        type_ = "";

        topic_ = "";

        totalMessage_ = 0L;

        actualMessage_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return proto.MessageFromBroker.internal_static_tutorial_MessageFromBrokerDetails_descriptor;
      }

      @java.lang.Override
      public proto.MessageFromBroker.MessageFromBrokerDetails getDefaultInstanceForType() {
        return proto.MessageFromBroker.MessageFromBrokerDetails.getDefaultInstance();
      }

      @java.lang.Override
      public proto.MessageFromBroker.MessageFromBrokerDetails build() {
        proto.MessageFromBroker.MessageFromBrokerDetails result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public proto.MessageFromBroker.MessageFromBrokerDetails buildPartial() {
        proto.MessageFromBroker.MessageFromBrokerDetails result = new proto.MessageFromBroker.MessageFromBrokerDetails(this);
        int from_bitField0_ = bitField0_;
        result.type_ = type_;
        result.topic_ = topic_;
        result.totalMessage_ = totalMessage_;
        if (((bitField0_ & 0x00000001) != 0)) {
          actualMessage_ = java.util.Collections.unmodifiableList(actualMessage_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.actualMessage_ = actualMessage_;
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
        if (other instanceof proto.MessageFromBroker.MessageFromBrokerDetails) {
          return mergeFrom((proto.MessageFromBroker.MessageFromBrokerDetails)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(proto.MessageFromBroker.MessageFromBrokerDetails other) {
        if (other == proto.MessageFromBroker.MessageFromBrokerDetails.getDefaultInstance()) return this;
        if (!other.getType().isEmpty()) {
          type_ = other.type_;
          onChanged();
        }
        if (!other.getTopic().isEmpty()) {
          topic_ = other.topic_;
          onChanged();
        }
        if (other.getTotalMessage() != 0L) {
          setTotalMessage(other.getTotalMessage());
        }
        if (!other.actualMessage_.isEmpty()) {
          if (actualMessage_.isEmpty()) {
            actualMessage_ = other.actualMessage_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureActualMessageIsMutable();
            actualMessage_.addAll(other.actualMessage_);
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
        proto.MessageFromBroker.MessageFromBrokerDetails parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (proto.MessageFromBroker.MessageFromBrokerDetails) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object type_ = "";
      /**
       * <code>string type = 1;</code>
       * @return The type.
       */
      public java.lang.String getType() {
        java.lang.Object ref = type_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          type_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string type = 1;</code>
       * @return The bytes for type.
       */
      public com.google.protobuf.ByteString
          getTypeBytes() {
        java.lang.Object ref = type_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          type_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string type = 1;</code>
       * @param value The type to set.
       * @return This builder for chaining.
       */
      public Builder setType(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string type = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearType() {
        
        type_ = getDefaultInstance().getType();
        onChanged();
        return this;
      }
      /**
       * <code>string type = 1;</code>
       * @param value The bytes for type to set.
       * @return This builder for chaining.
       */
      public Builder setTypeBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        type_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object topic_ = "";
      /**
       * <code>string topic = 2;</code>
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
       * <code>string topic = 2;</code>
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
       * <code>string topic = 2;</code>
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
       * <code>string topic = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearTopic() {
        
        topic_ = getDefaultInstance().getTopic();
        onChanged();
        return this;
      }
      /**
       * <code>string topic = 2;</code>
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

      private long totalMessage_ ;
      /**
       * <code>uint64 totalMessage = 3;</code>
       * @return The totalMessage.
       */
      @java.lang.Override
      public long getTotalMessage() {
        return totalMessage_;
      }
      /**
       * <code>uint64 totalMessage = 3;</code>
       * @param value The totalMessage to set.
       * @return This builder for chaining.
       */
      public Builder setTotalMessage(long value) {
        
        totalMessage_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint64 totalMessage = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearTotalMessage() {
        
        totalMessage_ = 0L;
        onChanged();
        return this;
      }

      private java.util.List<com.google.protobuf.ByteString> actualMessage_ = java.util.Collections.emptyList();
      private void ensureActualMessageIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          actualMessage_ = new java.util.ArrayList<com.google.protobuf.ByteString>(actualMessage_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated bytes actualMessage = 4;</code>
       * @return A list containing the actualMessage.
       */
      public java.util.List<com.google.protobuf.ByteString>
          getActualMessageList() {
        return ((bitField0_ & 0x00000001) != 0) ?
                 java.util.Collections.unmodifiableList(actualMessage_) : actualMessage_;
      }
      /**
       * <code>repeated bytes actualMessage = 4;</code>
       * @return The count of actualMessage.
       */
      public int getActualMessageCount() {
        return actualMessage_.size();
      }
      /**
       * <code>repeated bytes actualMessage = 4;</code>
       * @param index The index of the element to return.
       * @return The actualMessage at the given index.
       */
      public com.google.protobuf.ByteString getActualMessage(int index) {
        return actualMessage_.get(index);
      }
      /**
       * <code>repeated bytes actualMessage = 4;</code>
       * @param index The index to set the value at.
       * @param value The actualMessage to set.
       * @return This builder for chaining.
       */
      public Builder setActualMessage(
          int index, com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureActualMessageIsMutable();
        actualMessage_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes actualMessage = 4;</code>
       * @param value The actualMessage to add.
       * @return This builder for chaining.
       */
      public Builder addActualMessage(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureActualMessageIsMutable();
        actualMessage_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes actualMessage = 4;</code>
       * @param values The actualMessage to add.
       * @return This builder for chaining.
       */
      public Builder addAllActualMessage(
          java.lang.Iterable<? extends com.google.protobuf.ByteString> values) {
        ensureActualMessageIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, actualMessage_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes actualMessage = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearActualMessage() {
        actualMessage_ = java.util.Collections.emptyList();
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


      // @@protoc_insertion_point(builder_scope:tutorial.MessageFromBrokerDetails)
    }

    // @@protoc_insertion_point(class_scope:tutorial.MessageFromBrokerDetails)
    private static final proto.MessageFromBroker.MessageFromBrokerDetails DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new proto.MessageFromBroker.MessageFromBrokerDetails();
    }

    public static proto.MessageFromBroker.MessageFromBrokerDetails getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<MessageFromBrokerDetails>
        PARSER = new com.google.protobuf.AbstractParser<MessageFromBrokerDetails>() {
      @java.lang.Override
      public MessageFromBrokerDetails parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new MessageFromBrokerDetails(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<MessageFromBrokerDetails> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<MessageFromBrokerDetails> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public proto.MessageFromBroker.MessageFromBrokerDetails getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tutorial_MessageFromBrokerDetails_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tutorial_MessageFromBrokerDetails_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\027MessageFromBroker.proto\022\010tutorial\"d\n\030M" +
      "essageFromBrokerDetails\022\014\n\004type\030\001 \001(\t\022\r\n" +
      "\005topic\030\002 \001(\t\022\024\n\014totalMessage\030\003 \001(\004\022\025\n\rac" +
      "tualMessage\030\004 \003(\014B\032\n\005protoB\021MessageFromB" +
      "rokerb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_tutorial_MessageFromBrokerDetails_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tutorial_MessageFromBrokerDetails_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tutorial_MessageFromBrokerDetails_descriptor,
        new java.lang.String[] { "Type", "Topic", "TotalMessage", "ActualMessage", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
