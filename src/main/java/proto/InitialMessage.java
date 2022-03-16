// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InitialMessage.proto

package proto;

public final class InitialMessage {
  private InitialMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface InitialMessageDetailsOrBuilder extends
      // @@protoc_insertion_point(interface_extends:tutorial.InitialMessageDetails)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string connectionSender = 1;</code>
     * @return The connectionSender.
     */
    java.lang.String getConnectionSender();
    /**
     * <code>string connectionSender = 1;</code>
     * @return The bytes for connectionSender.
     */
    com.google.protobuf.ByteString
        getConnectionSenderBytes();

    /**
     * <code>string name = 2;</code>
     * @return The name.
     */
    java.lang.String getName();
    /**
     * <code>string name = 2;</code>
     * @return The bytes for name.
     */
    com.google.protobuf.ByteString
        getNameBytes();

    /**
     * <code>string consumerType = 3;</code>
     * @return The consumerType.
     */
    java.lang.String getConsumerType();
    /**
     * <code>string consumerType = 3;</code>
     * @return The bytes for consumerType.
     */
    com.google.protobuf.ByteString
        getConsumerTypeBytes();

    /**
     * <code>string topic = 4;</code>
     * @return The topic.
     */
    java.lang.String getTopic();
    /**
     * <code>string topic = 4;</code>
     * @return The bytes for topic.
     */
    com.google.protobuf.ByteString
        getTopicBytes();

    /**
     * <code>int32 initialOffset = 5;</code>
     * @return The initialOffset.
     */
    int getInitialOffset();
  }
  /**
   * Protobuf type {@code tutorial.InitialMessageDetails}
   */
  public static final class InitialMessageDetails extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:tutorial.InitialMessageDetails)
      InitialMessageDetailsOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use InitialMessageDetails.newBuilder() to construct.
    private InitialMessageDetails(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private InitialMessageDetails() {
      connectionSender_ = "";
      name_ = "";
      consumerType_ = "";
      topic_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new InitialMessageDetails();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private InitialMessageDetails(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
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

              connectionSender_ = s;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              name_ = s;
              break;
            }
            case 26: {
              java.lang.String s = input.readStringRequireUtf8();

              consumerType_ = s;
              break;
            }
            case 34: {
              java.lang.String s = input.readStringRequireUtf8();

              topic_ = s;
              break;
            }
            case 40: {

              initialOffset_ = input.readInt32();
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
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return proto.InitialMessage.internal_static_tutorial_InitialMessageDetails_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.InitialMessage.internal_static_tutorial_InitialMessageDetails_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.InitialMessage.InitialMessageDetails.class, proto.InitialMessage.InitialMessageDetails.Builder.class);
    }

    public static final int CONNECTIONSENDER_FIELD_NUMBER = 1;
    private volatile java.lang.Object connectionSender_;
    /**
     * <code>string connectionSender = 1;</code>
     * @return The connectionSender.
     */
    @java.lang.Override
    public java.lang.String getConnectionSender() {
      java.lang.Object ref = connectionSender_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        connectionSender_ = s;
        return s;
      }
    }
    /**
     * <code>string connectionSender = 1;</code>
     * @return The bytes for connectionSender.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getConnectionSenderBytes() {
      java.lang.Object ref = connectionSender_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        connectionSender_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int NAME_FIELD_NUMBER = 2;
    private volatile java.lang.Object name_;
    /**
     * <code>string name = 2;</code>
     * @return The name.
     */
    @java.lang.Override
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        name_ = s;
        return s;
      }
    }
    /**
     * <code>string name = 2;</code>
     * @return The bytes for name.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int CONSUMERTYPE_FIELD_NUMBER = 3;
    private volatile java.lang.Object consumerType_;
    /**
     * <code>string consumerType = 3;</code>
     * @return The consumerType.
     */
    @java.lang.Override
    public java.lang.String getConsumerType() {
      java.lang.Object ref = consumerType_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        consumerType_ = s;
        return s;
      }
    }
    /**
     * <code>string consumerType = 3;</code>
     * @return The bytes for consumerType.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getConsumerTypeBytes() {
      java.lang.Object ref = consumerType_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        consumerType_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TOPIC_FIELD_NUMBER = 4;
    private volatile java.lang.Object topic_;
    /**
     * <code>string topic = 4;</code>
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
     * <code>string topic = 4;</code>
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

    public static final int INITIALOFFSET_FIELD_NUMBER = 5;
    private int initialOffset_;
    /**
     * <code>int32 initialOffset = 5;</code>
     * @return The initialOffset.
     */
    @java.lang.Override
    public int getInitialOffset() {
      return initialOffset_;
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
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(connectionSender_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, connectionSender_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(name_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, name_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(consumerType_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, consumerType_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(topic_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, topic_);
      }
      if (initialOffset_ != 0) {
        output.writeInt32(5, initialOffset_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(connectionSender_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, connectionSender_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(name_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, name_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(consumerType_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, consumerType_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(topic_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, topic_);
      }
      if (initialOffset_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, initialOffset_);
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
      if (!(obj instanceof proto.InitialMessage.InitialMessageDetails)) {
        return super.equals(obj);
      }
      proto.InitialMessage.InitialMessageDetails other = (proto.InitialMessage.InitialMessageDetails) obj;

      if (!getConnectionSender()
          .equals(other.getConnectionSender())) return false;
      if (!getName()
          .equals(other.getName())) return false;
      if (!getConsumerType()
          .equals(other.getConsumerType())) return false;
      if (!getTopic()
          .equals(other.getTopic())) return false;
      if (getInitialOffset()
          != other.getInitialOffset()) return false;
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
      hash = (37 * hash) + CONNECTIONSENDER_FIELD_NUMBER;
      hash = (53 * hash) + getConnectionSender().hashCode();
      hash = (37 * hash) + NAME_FIELD_NUMBER;
      hash = (53 * hash) + getName().hashCode();
      hash = (37 * hash) + CONSUMERTYPE_FIELD_NUMBER;
      hash = (53 * hash) + getConsumerType().hashCode();
      hash = (37 * hash) + TOPIC_FIELD_NUMBER;
      hash = (53 * hash) + getTopic().hashCode();
      hash = (37 * hash) + INITIALOFFSET_FIELD_NUMBER;
      hash = (53 * hash) + getInitialOffset();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static proto.InitialMessage.InitialMessageDetails parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.InitialMessage.InitialMessageDetails parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.InitialMessage.InitialMessageDetails parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.InitialMessage.InitialMessageDetails parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.InitialMessage.InitialMessageDetails parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.InitialMessage.InitialMessageDetails parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.InitialMessage.InitialMessageDetails parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.InitialMessage.InitialMessageDetails parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.InitialMessage.InitialMessageDetails parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static proto.InitialMessage.InitialMessageDetails parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.InitialMessage.InitialMessageDetails parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.InitialMessage.InitialMessageDetails parseFrom(
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
    public static Builder newBuilder(proto.InitialMessage.InitialMessageDetails prototype) {
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
     * Protobuf type {@code tutorial.InitialMessageDetails}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:tutorial.InitialMessageDetails)
        proto.InitialMessage.InitialMessageDetailsOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return proto.InitialMessage.internal_static_tutorial_InitialMessageDetails_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return proto.InitialMessage.internal_static_tutorial_InitialMessageDetails_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                proto.InitialMessage.InitialMessageDetails.class, proto.InitialMessage.InitialMessageDetails.Builder.class);
      }

      // Construct using proto.InitialMessage.InitialMessageDetails.newBuilder()
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
        connectionSender_ = "";

        name_ = "";

        consumerType_ = "";

        topic_ = "";

        initialOffset_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return proto.InitialMessage.internal_static_tutorial_InitialMessageDetails_descriptor;
      }

      @java.lang.Override
      public proto.InitialMessage.InitialMessageDetails getDefaultInstanceForType() {
        return proto.InitialMessage.InitialMessageDetails.getDefaultInstance();
      }

      @java.lang.Override
      public proto.InitialMessage.InitialMessageDetails build() {
        proto.InitialMessage.InitialMessageDetails result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public proto.InitialMessage.InitialMessageDetails buildPartial() {
        proto.InitialMessage.InitialMessageDetails result = new proto.InitialMessage.InitialMessageDetails(this);
        result.connectionSender_ = connectionSender_;
        result.name_ = name_;
        result.consumerType_ = consumerType_;
        result.topic_ = topic_;
        result.initialOffset_ = initialOffset_;
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
        if (other instanceof proto.InitialMessage.InitialMessageDetails) {
          return mergeFrom((proto.InitialMessage.InitialMessageDetails)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(proto.InitialMessage.InitialMessageDetails other) {
        if (other == proto.InitialMessage.InitialMessageDetails.getDefaultInstance()) return this;
        if (!other.getConnectionSender().isEmpty()) {
          connectionSender_ = other.connectionSender_;
          onChanged();
        }
        if (!other.getName().isEmpty()) {
          name_ = other.name_;
          onChanged();
        }
        if (!other.getConsumerType().isEmpty()) {
          consumerType_ = other.consumerType_;
          onChanged();
        }
        if (!other.getTopic().isEmpty()) {
          topic_ = other.topic_;
          onChanged();
        }
        if (other.getInitialOffset() != 0) {
          setInitialOffset(other.getInitialOffset());
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
        proto.InitialMessage.InitialMessageDetails parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (proto.InitialMessage.InitialMessageDetails) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object connectionSender_ = "";
      /**
       * <code>string connectionSender = 1;</code>
       * @return The connectionSender.
       */
      public java.lang.String getConnectionSender() {
        java.lang.Object ref = connectionSender_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          connectionSender_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string connectionSender = 1;</code>
       * @return The bytes for connectionSender.
       */
      public com.google.protobuf.ByteString
          getConnectionSenderBytes() {
        java.lang.Object ref = connectionSender_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          connectionSender_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string connectionSender = 1;</code>
       * @param value The connectionSender to set.
       * @return This builder for chaining.
       */
      public Builder setConnectionSender(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        connectionSender_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string connectionSender = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearConnectionSender() {
        
        connectionSender_ = getDefaultInstance().getConnectionSender();
        onChanged();
        return this;
      }
      /**
       * <code>string connectionSender = 1;</code>
       * @param value The bytes for connectionSender to set.
       * @return This builder for chaining.
       */
      public Builder setConnectionSenderBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        connectionSender_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object name_ = "";
      /**
       * <code>string name = 2;</code>
       * @return The name.
       */
      public java.lang.String getName() {
        java.lang.Object ref = name_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          name_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string name = 2;</code>
       * @return The bytes for name.
       */
      public com.google.protobuf.ByteString
          getNameBytes() {
        java.lang.Object ref = name_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          name_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string name = 2;</code>
       * @param value The name to set.
       * @return This builder for chaining.
       */
      public Builder setName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        name_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string name = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearName() {
        
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      /**
       * <code>string name = 2;</code>
       * @param value The bytes for name to set.
       * @return This builder for chaining.
       */
      public Builder setNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        name_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object consumerType_ = "";
      /**
       * <code>string consumerType = 3;</code>
       * @return The consumerType.
       */
      public java.lang.String getConsumerType() {
        java.lang.Object ref = consumerType_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          consumerType_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string consumerType = 3;</code>
       * @return The bytes for consumerType.
       */
      public com.google.protobuf.ByteString
          getConsumerTypeBytes() {
        java.lang.Object ref = consumerType_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          consumerType_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string consumerType = 3;</code>
       * @param value The consumerType to set.
       * @return This builder for chaining.
       */
      public Builder setConsumerType(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        consumerType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string consumerType = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearConsumerType() {
        
        consumerType_ = getDefaultInstance().getConsumerType();
        onChanged();
        return this;
      }
      /**
       * <code>string consumerType = 3;</code>
       * @param value The bytes for consumerType to set.
       * @return This builder for chaining.
       */
      public Builder setConsumerTypeBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        consumerType_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object topic_ = "";
      /**
       * <code>string topic = 4;</code>
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
       * <code>string topic = 4;</code>
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
       * <code>string topic = 4;</code>
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
       * <code>string topic = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearTopic() {
        
        topic_ = getDefaultInstance().getTopic();
        onChanged();
        return this;
      }
      /**
       * <code>string topic = 4;</code>
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

      private int initialOffset_ ;
      /**
       * <code>int32 initialOffset = 5;</code>
       * @return The initialOffset.
       */
      @java.lang.Override
      public int getInitialOffset() {
        return initialOffset_;
      }
      /**
       * <code>int32 initialOffset = 5;</code>
       * @param value The initialOffset to set.
       * @return This builder for chaining.
       */
      public Builder setInitialOffset(int value) {
        
        initialOffset_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 initialOffset = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearInitialOffset() {
        
        initialOffset_ = 0;
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


      // @@protoc_insertion_point(builder_scope:tutorial.InitialMessageDetails)
    }

    // @@protoc_insertion_point(class_scope:tutorial.InitialMessageDetails)
    private static final proto.InitialMessage.InitialMessageDetails DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new proto.InitialMessage.InitialMessageDetails();
    }

    public static proto.InitialMessage.InitialMessageDetails getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<InitialMessageDetails>
        PARSER = new com.google.protobuf.AbstractParser<InitialMessageDetails>() {
      @java.lang.Override
      public InitialMessageDetails parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new InitialMessageDetails(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<InitialMessageDetails> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<InitialMessageDetails> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public proto.InitialMessage.InitialMessageDetails getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tutorial_InitialMessageDetails_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tutorial_InitialMessageDetails_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\024InitialMessage.proto\022\010tutorial\"{\n\025Init" +
      "ialMessageDetails\022\030\n\020connectionSender\030\001 " +
      "\001(\t\022\014\n\004name\030\002 \001(\t\022\024\n\014consumerType\030\003 \001(\t\022" +
      "\r\n\005topic\030\004 \001(\t\022\025\n\rinitialOffset\030\005 \001(\005B\027\n" +
      "\005protoB\016InitialMessageb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_tutorial_InitialMessageDetails_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tutorial_InitialMessageDetails_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tutorial_InitialMessageDetails_descriptor,
        new java.lang.String[] { "ConnectionSender", "Name", "ConsumerType", "Topic", "InitialOffset", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
