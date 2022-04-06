// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: UpdateLeaderInfo.proto

package proto;

public final class UpdateLeaderInfo {
  private UpdateLeaderInfo() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface UpdateLeaderInfoDetailsOrBuilder extends
      // @@protoc_insertion_point(interface_extends:tutorial.UpdateLeaderInfoDetails)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int32 messageId = 1;</code>
     * @return The messageId.
     */
    int getMessageId();

    /**
     * <code>string senderType = 2;</code>
     * @return The senderType.
     */
    java.lang.String getSenderType();
    /**
     * <code>string senderType = 2;</code>
     * @return The bytes for senderType.
     */
    com.google.protobuf.ByteString
        getSenderTypeBytes();

    /**
     * <code>string brokerName = 3;</code>
     * @return The brokerName.
     */
    java.lang.String getBrokerName();
    /**
     * <code>string brokerName = 3;</code>
     * @return The bytes for brokerName.
     */
    com.google.protobuf.ByteString
        getBrokerNameBytes();

    /**
     * <code>string brokerId = 4;</code>
     * @return The brokerId.
     */
    java.lang.String getBrokerId();
    /**
     * <code>string brokerId = 4;</code>
     * @return The bytes for brokerId.
     */
    com.google.protobuf.ByteString
        getBrokerIdBytes();

    /**
     * <code>string brokerIP = 5;</code>
     * @return The brokerIP.
     */
    java.lang.String getBrokerIP();
    /**
     * <code>string brokerIP = 5;</code>
     * @return The bytes for brokerIP.
     */
    com.google.protobuf.ByteString
        getBrokerIPBytes();

    /**
     * <code>int32 brokerPort = 6;</code>
     * @return The brokerPort.
     */
    int getBrokerPort();
  }
  /**
   * Protobuf type {@code tutorial.UpdateLeaderInfoDetails}
   */
  public static final class UpdateLeaderInfoDetails extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:tutorial.UpdateLeaderInfoDetails)
      UpdateLeaderInfoDetailsOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use UpdateLeaderInfoDetails.newBuilder() to construct.
    private UpdateLeaderInfoDetails(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private UpdateLeaderInfoDetails() {
      senderType_ = "";
      brokerName_ = "";
      brokerId_ = "";
      brokerIP_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new UpdateLeaderInfoDetails();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private UpdateLeaderInfoDetails(
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
            case 8: {

              messageId_ = input.readInt32();
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              senderType_ = s;
              break;
            }
            case 26: {
              java.lang.String s = input.readStringRequireUtf8();

              brokerName_ = s;
              break;
            }
            case 34: {
              java.lang.String s = input.readStringRequireUtf8();

              brokerId_ = s;
              break;
            }
            case 42: {
              java.lang.String s = input.readStringRequireUtf8();

              brokerIP_ = s;
              break;
            }
            case 48: {

              brokerPort_ = input.readInt32();
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
      return proto.UpdateLeaderInfo.internal_static_tutorial_UpdateLeaderInfoDetails_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.UpdateLeaderInfo.internal_static_tutorial_UpdateLeaderInfoDetails_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.UpdateLeaderInfo.UpdateLeaderInfoDetails.class, proto.UpdateLeaderInfo.UpdateLeaderInfoDetails.Builder.class);
    }

    public static final int MESSAGEID_FIELD_NUMBER = 1;
    private int messageId_;
    /**
     * <code>int32 messageId = 1;</code>
     * @return The messageId.
     */
    @java.lang.Override
    public int getMessageId() {
      return messageId_;
    }

    public static final int SENDERTYPE_FIELD_NUMBER = 2;
    private volatile java.lang.Object senderType_;
    /**
     * <code>string senderType = 2;</code>
     * @return The senderType.
     */
    @java.lang.Override
    public java.lang.String getSenderType() {
      java.lang.Object ref = senderType_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        senderType_ = s;
        return s;
      }
    }
    /**
     * <code>string senderType = 2;</code>
     * @return The bytes for senderType.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getSenderTypeBytes() {
      java.lang.Object ref = senderType_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        senderType_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int BROKERNAME_FIELD_NUMBER = 3;
    private volatile java.lang.Object brokerName_;
    /**
     * <code>string brokerName = 3;</code>
     * @return The brokerName.
     */
    @java.lang.Override
    public java.lang.String getBrokerName() {
      java.lang.Object ref = brokerName_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        brokerName_ = s;
        return s;
      }
    }
    /**
     * <code>string brokerName = 3;</code>
     * @return The bytes for brokerName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getBrokerNameBytes() {
      java.lang.Object ref = brokerName_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        brokerName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int BROKERID_FIELD_NUMBER = 4;
    private volatile java.lang.Object brokerId_;
    /**
     * <code>string brokerId = 4;</code>
     * @return The brokerId.
     */
    @java.lang.Override
    public java.lang.String getBrokerId() {
      java.lang.Object ref = brokerId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        brokerId_ = s;
        return s;
      }
    }
    /**
     * <code>string brokerId = 4;</code>
     * @return The bytes for brokerId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getBrokerIdBytes() {
      java.lang.Object ref = brokerId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        brokerId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int BROKERIP_FIELD_NUMBER = 5;
    private volatile java.lang.Object brokerIP_;
    /**
     * <code>string brokerIP = 5;</code>
     * @return The brokerIP.
     */
    @java.lang.Override
    public java.lang.String getBrokerIP() {
      java.lang.Object ref = brokerIP_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        brokerIP_ = s;
        return s;
      }
    }
    /**
     * <code>string brokerIP = 5;</code>
     * @return The bytes for brokerIP.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getBrokerIPBytes() {
      java.lang.Object ref = brokerIP_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        brokerIP_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int BROKERPORT_FIELD_NUMBER = 6;
    private int brokerPort_;
    /**
     * <code>int32 brokerPort = 6;</code>
     * @return The brokerPort.
     */
    @java.lang.Override
    public int getBrokerPort() {
      return brokerPort_;
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
      if (messageId_ != 0) {
        output.writeInt32(1, messageId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(senderType_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, senderType_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(brokerName_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, brokerName_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(brokerId_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, brokerId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(brokerIP_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 5, brokerIP_);
      }
      if (brokerPort_ != 0) {
        output.writeInt32(6, brokerPort_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (messageId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, messageId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(senderType_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, senderType_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(brokerName_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, brokerName_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(brokerId_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, brokerId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(brokerIP_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, brokerIP_);
      }
      if (brokerPort_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(6, brokerPort_);
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
      if (!(obj instanceof proto.UpdateLeaderInfo.UpdateLeaderInfoDetails)) {
        return super.equals(obj);
      }
      proto.UpdateLeaderInfo.UpdateLeaderInfoDetails other = (proto.UpdateLeaderInfo.UpdateLeaderInfoDetails) obj;

      if (getMessageId()
          != other.getMessageId()) return false;
      if (!getSenderType()
          .equals(other.getSenderType())) return false;
      if (!getBrokerName()
          .equals(other.getBrokerName())) return false;
      if (!getBrokerId()
          .equals(other.getBrokerId())) return false;
      if (!getBrokerIP()
          .equals(other.getBrokerIP())) return false;
      if (getBrokerPort()
          != other.getBrokerPort()) return false;
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
      hash = (37 * hash) + MESSAGEID_FIELD_NUMBER;
      hash = (53 * hash) + getMessageId();
      hash = (37 * hash) + SENDERTYPE_FIELD_NUMBER;
      hash = (53 * hash) + getSenderType().hashCode();
      hash = (37 * hash) + BROKERNAME_FIELD_NUMBER;
      hash = (53 * hash) + getBrokerName().hashCode();
      hash = (37 * hash) + BROKERID_FIELD_NUMBER;
      hash = (53 * hash) + getBrokerId().hashCode();
      hash = (37 * hash) + BROKERIP_FIELD_NUMBER;
      hash = (53 * hash) + getBrokerIP().hashCode();
      hash = (37 * hash) + BROKERPORT_FIELD_NUMBER;
      hash = (53 * hash) + getBrokerPort();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parseFrom(
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
    public static Builder newBuilder(proto.UpdateLeaderInfo.UpdateLeaderInfoDetails prototype) {
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
     * Protobuf type {@code tutorial.UpdateLeaderInfoDetails}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:tutorial.UpdateLeaderInfoDetails)
        proto.UpdateLeaderInfo.UpdateLeaderInfoDetailsOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return proto.UpdateLeaderInfo.internal_static_tutorial_UpdateLeaderInfoDetails_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return proto.UpdateLeaderInfo.internal_static_tutorial_UpdateLeaderInfoDetails_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                proto.UpdateLeaderInfo.UpdateLeaderInfoDetails.class, proto.UpdateLeaderInfo.UpdateLeaderInfoDetails.Builder.class);
      }

      // Construct using proto.UpdateLeaderInfo.UpdateLeaderInfoDetails.newBuilder()
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
        messageId_ = 0;

        senderType_ = "";

        brokerName_ = "";

        brokerId_ = "";

        brokerIP_ = "";

        brokerPort_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return proto.UpdateLeaderInfo.internal_static_tutorial_UpdateLeaderInfoDetails_descriptor;
      }

      @java.lang.Override
      public proto.UpdateLeaderInfo.UpdateLeaderInfoDetails getDefaultInstanceForType() {
        return proto.UpdateLeaderInfo.UpdateLeaderInfoDetails.getDefaultInstance();
      }

      @java.lang.Override
      public proto.UpdateLeaderInfo.UpdateLeaderInfoDetails build() {
        proto.UpdateLeaderInfo.UpdateLeaderInfoDetails result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public proto.UpdateLeaderInfo.UpdateLeaderInfoDetails buildPartial() {
        proto.UpdateLeaderInfo.UpdateLeaderInfoDetails result = new proto.UpdateLeaderInfo.UpdateLeaderInfoDetails(this);
        result.messageId_ = messageId_;
        result.senderType_ = senderType_;
        result.brokerName_ = brokerName_;
        result.brokerId_ = brokerId_;
        result.brokerIP_ = brokerIP_;
        result.brokerPort_ = brokerPort_;
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
        if (other instanceof proto.UpdateLeaderInfo.UpdateLeaderInfoDetails) {
          return mergeFrom((proto.UpdateLeaderInfo.UpdateLeaderInfoDetails)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(proto.UpdateLeaderInfo.UpdateLeaderInfoDetails other) {
        if (other == proto.UpdateLeaderInfo.UpdateLeaderInfoDetails.getDefaultInstance()) return this;
        if (other.getMessageId() != 0) {
          setMessageId(other.getMessageId());
        }
        if (!other.getSenderType().isEmpty()) {
          senderType_ = other.senderType_;
          onChanged();
        }
        if (!other.getBrokerName().isEmpty()) {
          brokerName_ = other.brokerName_;
          onChanged();
        }
        if (!other.getBrokerId().isEmpty()) {
          brokerId_ = other.brokerId_;
          onChanged();
        }
        if (!other.getBrokerIP().isEmpty()) {
          brokerIP_ = other.brokerIP_;
          onChanged();
        }
        if (other.getBrokerPort() != 0) {
          setBrokerPort(other.getBrokerPort());
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
        proto.UpdateLeaderInfo.UpdateLeaderInfoDetails parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (proto.UpdateLeaderInfo.UpdateLeaderInfoDetails) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int messageId_ ;
      /**
       * <code>int32 messageId = 1;</code>
       * @return The messageId.
       */
      @java.lang.Override
      public int getMessageId() {
        return messageId_;
      }
      /**
       * <code>int32 messageId = 1;</code>
       * @param value The messageId to set.
       * @return This builder for chaining.
       */
      public Builder setMessageId(int value) {
        
        messageId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 messageId = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessageId() {
        
        messageId_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object senderType_ = "";
      /**
       * <code>string senderType = 2;</code>
       * @return The senderType.
       */
      public java.lang.String getSenderType() {
        java.lang.Object ref = senderType_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          senderType_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string senderType = 2;</code>
       * @return The bytes for senderType.
       */
      public com.google.protobuf.ByteString
          getSenderTypeBytes() {
        java.lang.Object ref = senderType_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          senderType_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string senderType = 2;</code>
       * @param value The senderType to set.
       * @return This builder for chaining.
       */
      public Builder setSenderType(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        senderType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string senderType = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearSenderType() {
        
        senderType_ = getDefaultInstance().getSenderType();
        onChanged();
        return this;
      }
      /**
       * <code>string senderType = 2;</code>
       * @param value The bytes for senderType to set.
       * @return This builder for chaining.
       */
      public Builder setSenderTypeBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        senderType_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object brokerName_ = "";
      /**
       * <code>string brokerName = 3;</code>
       * @return The brokerName.
       */
      public java.lang.String getBrokerName() {
        java.lang.Object ref = brokerName_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          brokerName_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string brokerName = 3;</code>
       * @return The bytes for brokerName.
       */
      public com.google.protobuf.ByteString
          getBrokerNameBytes() {
        java.lang.Object ref = brokerName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          brokerName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string brokerName = 3;</code>
       * @param value The brokerName to set.
       * @return This builder for chaining.
       */
      public Builder setBrokerName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        brokerName_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string brokerName = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearBrokerName() {
        
        brokerName_ = getDefaultInstance().getBrokerName();
        onChanged();
        return this;
      }
      /**
       * <code>string brokerName = 3;</code>
       * @param value The bytes for brokerName to set.
       * @return This builder for chaining.
       */
      public Builder setBrokerNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        brokerName_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object brokerId_ = "";
      /**
       * <code>string brokerId = 4;</code>
       * @return The brokerId.
       */
      public java.lang.String getBrokerId() {
        java.lang.Object ref = brokerId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          brokerId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string brokerId = 4;</code>
       * @return The bytes for brokerId.
       */
      public com.google.protobuf.ByteString
          getBrokerIdBytes() {
        java.lang.Object ref = brokerId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          brokerId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string brokerId = 4;</code>
       * @param value The brokerId to set.
       * @return This builder for chaining.
       */
      public Builder setBrokerId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        brokerId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string brokerId = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearBrokerId() {
        
        brokerId_ = getDefaultInstance().getBrokerId();
        onChanged();
        return this;
      }
      /**
       * <code>string brokerId = 4;</code>
       * @param value The bytes for brokerId to set.
       * @return This builder for chaining.
       */
      public Builder setBrokerIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        brokerId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object brokerIP_ = "";
      /**
       * <code>string brokerIP = 5;</code>
       * @return The brokerIP.
       */
      public java.lang.String getBrokerIP() {
        java.lang.Object ref = brokerIP_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          brokerIP_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string brokerIP = 5;</code>
       * @return The bytes for brokerIP.
       */
      public com.google.protobuf.ByteString
          getBrokerIPBytes() {
        java.lang.Object ref = brokerIP_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          brokerIP_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string brokerIP = 5;</code>
       * @param value The brokerIP to set.
       * @return This builder for chaining.
       */
      public Builder setBrokerIP(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        brokerIP_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string brokerIP = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearBrokerIP() {
        
        brokerIP_ = getDefaultInstance().getBrokerIP();
        onChanged();
        return this;
      }
      /**
       * <code>string brokerIP = 5;</code>
       * @param value The bytes for brokerIP to set.
       * @return This builder for chaining.
       */
      public Builder setBrokerIPBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        brokerIP_ = value;
        onChanged();
        return this;
      }

      private int brokerPort_ ;
      /**
       * <code>int32 brokerPort = 6;</code>
       * @return The brokerPort.
       */
      @java.lang.Override
      public int getBrokerPort() {
        return brokerPort_;
      }
      /**
       * <code>int32 brokerPort = 6;</code>
       * @param value The brokerPort to set.
       * @return This builder for chaining.
       */
      public Builder setBrokerPort(int value) {
        
        brokerPort_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 brokerPort = 6;</code>
       * @return This builder for chaining.
       */
      public Builder clearBrokerPort() {
        
        brokerPort_ = 0;
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


      // @@protoc_insertion_point(builder_scope:tutorial.UpdateLeaderInfoDetails)
    }

    // @@protoc_insertion_point(class_scope:tutorial.UpdateLeaderInfoDetails)
    private static final proto.UpdateLeaderInfo.UpdateLeaderInfoDetails DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new proto.UpdateLeaderInfo.UpdateLeaderInfoDetails();
    }

    public static proto.UpdateLeaderInfo.UpdateLeaderInfoDetails getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<UpdateLeaderInfoDetails>
        PARSER = new com.google.protobuf.AbstractParser<UpdateLeaderInfoDetails>() {
      @java.lang.Override
      public UpdateLeaderInfoDetails parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new UpdateLeaderInfoDetails(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<UpdateLeaderInfoDetails> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<UpdateLeaderInfoDetails> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public proto.UpdateLeaderInfo.UpdateLeaderInfoDetails getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tutorial_UpdateLeaderInfoDetails_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tutorial_UpdateLeaderInfoDetails_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026UpdateLeaderInfo.proto\022\010tutorial\"\214\001\n\027U" +
      "pdateLeaderInfoDetails\022\021\n\tmessageId\030\001 \001(" +
      "\005\022\022\n\nsenderType\030\002 \001(\t\022\022\n\nbrokerName\030\003 \001(" +
      "\t\022\020\n\010brokerId\030\004 \001(\t\022\020\n\010brokerIP\030\005 \001(\t\022\022\n" +
      "\nbrokerPort\030\006 \001(\005B\031\n\005protoB\020UpdateLeader" +
      "Infob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_tutorial_UpdateLeaderInfoDetails_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tutorial_UpdateLeaderInfoDetails_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tutorial_UpdateLeaderInfoDetails_descriptor,
        new java.lang.String[] { "MessageId", "SenderType", "BrokerName", "BrokerId", "BrokerIP", "BrokerPort", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}