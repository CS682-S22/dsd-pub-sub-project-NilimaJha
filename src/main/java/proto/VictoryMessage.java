// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: VictoryMessage.proto

package proto;

public final class VictoryMessage {
  private VictoryMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface VictoryMessageDetailsOrBuilder extends
      // @@protoc_insertion_point(interface_extends:tutorial.VictoryMessageDetails)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 newLeaderId = 1;</code>
     * @return The newLeaderId.
     */
    int getNewLeaderId();

    /**
     * <code>string newLeaderName = 2;</code>
     * @return The newLeaderName.
     */
    java.lang.String getNewLeaderName();
    /**
     * <code>string newLeaderName = 2;</code>
     * @return The bytes for newLeaderName.
     */
    com.google.protobuf.ByteString
        getNewLeaderNameBytes();

    /**
     * <code>string newLeaderIp = 3;</code>
     * @return The newLeaderIp.
     */
    java.lang.String getNewLeaderIp();
    /**
     * <code>string newLeaderIp = 3;</code>
     * @return The bytes for newLeaderIp.
     */
    com.google.protobuf.ByteString
        getNewLeaderIpBytes();

    /**
     * <code>uint32 newLeaderPort = 4;</code>
     * @return The newLeaderPort.
     */
    int getNewLeaderPort();
  }
  /**
   * Protobuf type {@code tutorial.VictoryMessageDetails}
   */
  public static final class VictoryMessageDetails extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:tutorial.VictoryMessageDetails)
      VictoryMessageDetailsOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use VictoryMessageDetails.newBuilder() to construct.
    private VictoryMessageDetails(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private VictoryMessageDetails() {
      newLeaderName_ = "";
      newLeaderIp_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new VictoryMessageDetails();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private VictoryMessageDetails(
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

              newLeaderId_ = input.readUInt32();
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              newLeaderName_ = s;
              break;
            }
            case 26: {
              java.lang.String s = input.readStringRequireUtf8();

              newLeaderIp_ = s;
              break;
            }
            case 32: {

              newLeaderPort_ = input.readUInt32();
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
      return proto.VictoryMessage.internal_static_tutorial_VictoryMessageDetails_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.VictoryMessage.internal_static_tutorial_VictoryMessageDetails_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.VictoryMessage.VictoryMessageDetails.class, proto.VictoryMessage.VictoryMessageDetails.Builder.class);
    }

    public static final int NEWLEADERID_FIELD_NUMBER = 1;
    private int newLeaderId_;
    /**
     * <code>uint32 newLeaderId = 1;</code>
     * @return The newLeaderId.
     */
    @java.lang.Override
    public int getNewLeaderId() {
      return newLeaderId_;
    }

    public static final int NEWLEADERNAME_FIELD_NUMBER = 2;
    private volatile java.lang.Object newLeaderName_;
    /**
     * <code>string newLeaderName = 2;</code>
     * @return The newLeaderName.
     */
    @java.lang.Override
    public java.lang.String getNewLeaderName() {
      java.lang.Object ref = newLeaderName_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        newLeaderName_ = s;
        return s;
      }
    }
    /**
     * <code>string newLeaderName = 2;</code>
     * @return The bytes for newLeaderName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getNewLeaderNameBytes() {
      java.lang.Object ref = newLeaderName_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        newLeaderName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int NEWLEADERIP_FIELD_NUMBER = 3;
    private volatile java.lang.Object newLeaderIp_;
    /**
     * <code>string newLeaderIp = 3;</code>
     * @return The newLeaderIp.
     */
    @java.lang.Override
    public java.lang.String getNewLeaderIp() {
      java.lang.Object ref = newLeaderIp_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        newLeaderIp_ = s;
        return s;
      }
    }
    /**
     * <code>string newLeaderIp = 3;</code>
     * @return The bytes for newLeaderIp.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getNewLeaderIpBytes() {
      java.lang.Object ref = newLeaderIp_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        newLeaderIp_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int NEWLEADERPORT_FIELD_NUMBER = 4;
    private int newLeaderPort_;
    /**
     * <code>uint32 newLeaderPort = 4;</code>
     * @return The newLeaderPort.
     */
    @java.lang.Override
    public int getNewLeaderPort() {
      return newLeaderPort_;
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
      if (newLeaderId_ != 0) {
        output.writeUInt32(1, newLeaderId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(newLeaderName_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, newLeaderName_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(newLeaderIp_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, newLeaderIp_);
      }
      if (newLeaderPort_ != 0) {
        output.writeUInt32(4, newLeaderPort_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (newLeaderId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, newLeaderId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(newLeaderName_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, newLeaderName_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(newLeaderIp_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, newLeaderIp_);
      }
      if (newLeaderPort_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(4, newLeaderPort_);
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
      if (!(obj instanceof proto.VictoryMessage.VictoryMessageDetails)) {
        return super.equals(obj);
      }
      proto.VictoryMessage.VictoryMessageDetails other = (proto.VictoryMessage.VictoryMessageDetails) obj;

      if (getNewLeaderId()
          != other.getNewLeaderId()) return false;
      if (!getNewLeaderName()
          .equals(other.getNewLeaderName())) return false;
      if (!getNewLeaderIp()
          .equals(other.getNewLeaderIp())) return false;
      if (getNewLeaderPort()
          != other.getNewLeaderPort()) return false;
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
      hash = (37 * hash) + NEWLEADERID_FIELD_NUMBER;
      hash = (53 * hash) + getNewLeaderId();
      hash = (37 * hash) + NEWLEADERNAME_FIELD_NUMBER;
      hash = (53 * hash) + getNewLeaderName().hashCode();
      hash = (37 * hash) + NEWLEADERIP_FIELD_NUMBER;
      hash = (53 * hash) + getNewLeaderIp().hashCode();
      hash = (37 * hash) + NEWLEADERPORT_FIELD_NUMBER;
      hash = (53 * hash) + getNewLeaderPort();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.VictoryMessage.VictoryMessageDetails parseFrom(
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
    public static Builder newBuilder(proto.VictoryMessage.VictoryMessageDetails prototype) {
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
     * Protobuf type {@code tutorial.VictoryMessageDetails}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:tutorial.VictoryMessageDetails)
        proto.VictoryMessage.VictoryMessageDetailsOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return proto.VictoryMessage.internal_static_tutorial_VictoryMessageDetails_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return proto.VictoryMessage.internal_static_tutorial_VictoryMessageDetails_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                proto.VictoryMessage.VictoryMessageDetails.class, proto.VictoryMessage.VictoryMessageDetails.Builder.class);
      }

      // Construct using proto.VictoryMessage.VictoryMessageDetails.newBuilder()
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
        newLeaderId_ = 0;

        newLeaderName_ = "";

        newLeaderIp_ = "";

        newLeaderPort_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return proto.VictoryMessage.internal_static_tutorial_VictoryMessageDetails_descriptor;
      }

      @java.lang.Override
      public proto.VictoryMessage.VictoryMessageDetails getDefaultInstanceForType() {
        return proto.VictoryMessage.VictoryMessageDetails.getDefaultInstance();
      }

      @java.lang.Override
      public proto.VictoryMessage.VictoryMessageDetails build() {
        proto.VictoryMessage.VictoryMessageDetails result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public proto.VictoryMessage.VictoryMessageDetails buildPartial() {
        proto.VictoryMessage.VictoryMessageDetails result = new proto.VictoryMessage.VictoryMessageDetails(this);
        result.newLeaderId_ = newLeaderId_;
        result.newLeaderName_ = newLeaderName_;
        result.newLeaderIp_ = newLeaderIp_;
        result.newLeaderPort_ = newLeaderPort_;
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
        if (other instanceof proto.VictoryMessage.VictoryMessageDetails) {
          return mergeFrom((proto.VictoryMessage.VictoryMessageDetails)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(proto.VictoryMessage.VictoryMessageDetails other) {
        if (other == proto.VictoryMessage.VictoryMessageDetails.getDefaultInstance()) return this;
        if (other.getNewLeaderId() != 0) {
          setNewLeaderId(other.getNewLeaderId());
        }
        if (!other.getNewLeaderName().isEmpty()) {
          newLeaderName_ = other.newLeaderName_;
          onChanged();
        }
        if (!other.getNewLeaderIp().isEmpty()) {
          newLeaderIp_ = other.newLeaderIp_;
          onChanged();
        }
        if (other.getNewLeaderPort() != 0) {
          setNewLeaderPort(other.getNewLeaderPort());
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
        proto.VictoryMessage.VictoryMessageDetails parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (proto.VictoryMessage.VictoryMessageDetails) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int newLeaderId_ ;
      /**
       * <code>uint32 newLeaderId = 1;</code>
       * @return The newLeaderId.
       */
      @java.lang.Override
      public int getNewLeaderId() {
        return newLeaderId_;
      }
      /**
       * <code>uint32 newLeaderId = 1;</code>
       * @param value The newLeaderId to set.
       * @return This builder for chaining.
       */
      public Builder setNewLeaderId(int value) {
        
        newLeaderId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 newLeaderId = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearNewLeaderId() {
        
        newLeaderId_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object newLeaderName_ = "";
      /**
       * <code>string newLeaderName = 2;</code>
       * @return The newLeaderName.
       */
      public java.lang.String getNewLeaderName() {
        java.lang.Object ref = newLeaderName_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          newLeaderName_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string newLeaderName = 2;</code>
       * @return The bytes for newLeaderName.
       */
      public com.google.protobuf.ByteString
          getNewLeaderNameBytes() {
        java.lang.Object ref = newLeaderName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          newLeaderName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string newLeaderName = 2;</code>
       * @param value The newLeaderName to set.
       * @return This builder for chaining.
       */
      public Builder setNewLeaderName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        newLeaderName_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string newLeaderName = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearNewLeaderName() {
        
        newLeaderName_ = getDefaultInstance().getNewLeaderName();
        onChanged();
        return this;
      }
      /**
       * <code>string newLeaderName = 2;</code>
       * @param value The bytes for newLeaderName to set.
       * @return This builder for chaining.
       */
      public Builder setNewLeaderNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        newLeaderName_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object newLeaderIp_ = "";
      /**
       * <code>string newLeaderIp = 3;</code>
       * @return The newLeaderIp.
       */
      public java.lang.String getNewLeaderIp() {
        java.lang.Object ref = newLeaderIp_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          newLeaderIp_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string newLeaderIp = 3;</code>
       * @return The bytes for newLeaderIp.
       */
      public com.google.protobuf.ByteString
          getNewLeaderIpBytes() {
        java.lang.Object ref = newLeaderIp_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          newLeaderIp_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string newLeaderIp = 3;</code>
       * @param value The newLeaderIp to set.
       * @return This builder for chaining.
       */
      public Builder setNewLeaderIp(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        newLeaderIp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string newLeaderIp = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearNewLeaderIp() {
        
        newLeaderIp_ = getDefaultInstance().getNewLeaderIp();
        onChanged();
        return this;
      }
      /**
       * <code>string newLeaderIp = 3;</code>
       * @param value The bytes for newLeaderIp to set.
       * @return This builder for chaining.
       */
      public Builder setNewLeaderIpBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        newLeaderIp_ = value;
        onChanged();
        return this;
      }

      private int newLeaderPort_ ;
      /**
       * <code>uint32 newLeaderPort = 4;</code>
       * @return The newLeaderPort.
       */
      @java.lang.Override
      public int getNewLeaderPort() {
        return newLeaderPort_;
      }
      /**
       * <code>uint32 newLeaderPort = 4;</code>
       * @param value The newLeaderPort to set.
       * @return This builder for chaining.
       */
      public Builder setNewLeaderPort(int value) {
        
        newLeaderPort_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 newLeaderPort = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearNewLeaderPort() {
        
        newLeaderPort_ = 0;
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


      // @@protoc_insertion_point(builder_scope:tutorial.VictoryMessageDetails)
    }

    // @@protoc_insertion_point(class_scope:tutorial.VictoryMessageDetails)
    private static final proto.VictoryMessage.VictoryMessageDetails DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new proto.VictoryMessage.VictoryMessageDetails();
    }

    public static proto.VictoryMessage.VictoryMessageDetails getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<VictoryMessageDetails>
        PARSER = new com.google.protobuf.AbstractParser<VictoryMessageDetails>() {
      @java.lang.Override
      public VictoryMessageDetails parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new VictoryMessageDetails(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<VictoryMessageDetails> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<VictoryMessageDetails> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public proto.VictoryMessage.VictoryMessageDetails getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tutorial_VictoryMessageDetails_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tutorial_VictoryMessageDetails_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\024VictoryMessage.proto\022\010tutorial\"o\n\025Vict" +
      "oryMessageDetails\022\023\n\013newLeaderId\030\001 \001(\r\022\025" +
      "\n\rnewLeaderName\030\002 \001(\t\022\023\n\013newLeaderIp\030\003 \001" +
      "(\t\022\025\n\rnewLeaderPort\030\004 \001(\rB\027\n\005protoB\016Vict" +
      "oryMessageb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_tutorial_VictoryMessageDetails_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tutorial_VictoryMessageDetails_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tutorial_VictoryMessageDetails_descriptor,
        new java.lang.String[] { "NewLeaderId", "NewLeaderName", "NewLeaderIp", "NewLeaderPort", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}