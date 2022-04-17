// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: InitialSetupDone.proto

package proto;

public final class InitialSetupDone {
  private InitialSetupDone() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface InitialSetupDoneDetailsOrBuilder extends
      // @@protoc_insertion_point(interface_extends:tutorial.InitialSetupDoneDetails)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 messageId = 1;</code>
     * @return The messageId.
     */
    int getMessageId();

    /**
     * <code>bool done = 2;</code>
     * @return The done.
     */
    boolean getDone();

    /**
     * <code>uint64 currentOffSet = 3;</code>
     * @return The currentOffSet.
     */
    long getCurrentOffSet();

    /**
     * <code>repeated string topics = 4;</code>
     * @return A list containing the topics.
     */
    java.util.List<java.lang.String>
        getTopicsList();
    /**
     * <code>repeated string topics = 4;</code>
     * @return The count of topics.
     */
    int getTopicsCount();
    /**
     * <code>repeated string topics = 4;</code>
     * @param index The index of the element to return.
     * @return The topics at the given index.
     */
    java.lang.String getTopics(int index);
    /**
     * <code>repeated string topics = 4;</code>
     * @param index The index of the value to return.
     * @return The bytes of the topics at the given index.
     */
    com.google.protobuf.ByteString
        getTopicsBytes(int index);
  }
  /**
   * Protobuf type {@code tutorial.InitialSetupDoneDetails}
   */
  public static final class InitialSetupDoneDetails extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:tutorial.InitialSetupDoneDetails)
      InitialSetupDoneDetailsOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use InitialSetupDoneDetails.newBuilder() to construct.
    private InitialSetupDoneDetails(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private InitialSetupDoneDetails() {
      topics_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new InitialSetupDoneDetails();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private InitialSetupDoneDetails(
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

              messageId_ = input.readUInt32();
              break;
            }
            case 16: {

              done_ = input.readBool();
              break;
            }
            case 24: {

              currentOffSet_ = input.readUInt64();
              break;
            }
            case 34: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                topics_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              topics_.add(s);
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
          topics_ = topics_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return proto.InitialSetupDone.internal_static_tutorial_InitialSetupDoneDetails_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.InitialSetupDone.internal_static_tutorial_InitialSetupDoneDetails_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.InitialSetupDone.InitialSetupDoneDetails.class, proto.InitialSetupDone.InitialSetupDoneDetails.Builder.class);
    }

    public static final int MESSAGEID_FIELD_NUMBER = 1;
    private int messageId_;
    /**
     * <code>uint32 messageId = 1;</code>
     * @return The messageId.
     */
    @java.lang.Override
    public int getMessageId() {
      return messageId_;
    }

    public static final int DONE_FIELD_NUMBER = 2;
    private boolean done_;
    /**
     * <code>bool done = 2;</code>
     * @return The done.
     */
    @java.lang.Override
    public boolean getDone() {
      return done_;
    }

    public static final int CURRENTOFFSET_FIELD_NUMBER = 3;
    private long currentOffSet_;
    /**
     * <code>uint64 currentOffSet = 3;</code>
     * @return The currentOffSet.
     */
    @java.lang.Override
    public long getCurrentOffSet() {
      return currentOffSet_;
    }

    public static final int TOPICS_FIELD_NUMBER = 4;
    private com.google.protobuf.LazyStringList topics_;
    /**
     * <code>repeated string topics = 4;</code>
     * @return A list containing the topics.
     */
    public com.google.protobuf.ProtocolStringList
        getTopicsList() {
      return topics_;
    }
    /**
     * <code>repeated string topics = 4;</code>
     * @return The count of topics.
     */
    public int getTopicsCount() {
      return topics_.size();
    }
    /**
     * <code>repeated string topics = 4;</code>
     * @param index The index of the element to return.
     * @return The topics at the given index.
     */
    public java.lang.String getTopics(int index) {
      return topics_.get(index);
    }
    /**
     * <code>repeated string topics = 4;</code>
     * @param index The index of the value to return.
     * @return The bytes of the topics at the given index.
     */
    public com.google.protobuf.ByteString
        getTopicsBytes(int index) {
      return topics_.getByteString(index);
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
        output.writeUInt32(1, messageId_);
      }
      if (done_ != false) {
        output.writeBool(2, done_);
      }
      if (currentOffSet_ != 0L) {
        output.writeUInt64(3, currentOffSet_);
      }
      for (int i = 0; i < topics_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, topics_.getRaw(i));
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
          .computeUInt32Size(1, messageId_);
      }
      if (done_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(2, done_);
      }
      if (currentOffSet_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(3, currentOffSet_);
      }
      {
        int dataSize = 0;
        for (int i = 0; i < topics_.size(); i++) {
          dataSize += computeStringSizeNoTag(topics_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTopicsList().size();
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
      if (!(obj instanceof proto.InitialSetupDone.InitialSetupDoneDetails)) {
        return super.equals(obj);
      }
      proto.InitialSetupDone.InitialSetupDoneDetails other = (proto.InitialSetupDone.InitialSetupDoneDetails) obj;

      if (getMessageId()
          != other.getMessageId()) return false;
      if (getDone()
          != other.getDone()) return false;
      if (getCurrentOffSet()
          != other.getCurrentOffSet()) return false;
      if (!getTopicsList()
          .equals(other.getTopicsList())) return false;
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
      hash = (37 * hash) + DONE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getDone());
      hash = (37 * hash) + CURRENTOFFSET_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getCurrentOffSet());
      if (getTopicsCount() > 0) {
        hash = (37 * hash) + TOPICS_FIELD_NUMBER;
        hash = (53 * hash) + getTopicsList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.InitialSetupDone.InitialSetupDoneDetails parseFrom(
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
    public static Builder newBuilder(proto.InitialSetupDone.InitialSetupDoneDetails prototype) {
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
     * Protobuf type {@code tutorial.InitialSetupDoneDetails}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:tutorial.InitialSetupDoneDetails)
        proto.InitialSetupDone.InitialSetupDoneDetailsOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return proto.InitialSetupDone.internal_static_tutorial_InitialSetupDoneDetails_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return proto.InitialSetupDone.internal_static_tutorial_InitialSetupDoneDetails_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                proto.InitialSetupDone.InitialSetupDoneDetails.class, proto.InitialSetupDone.InitialSetupDoneDetails.Builder.class);
      }

      // Construct using proto.InitialSetupDone.InitialSetupDoneDetails.newBuilder()
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

        done_ = false;

        currentOffSet_ = 0L;

        topics_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return proto.InitialSetupDone.internal_static_tutorial_InitialSetupDoneDetails_descriptor;
      }

      @java.lang.Override
      public proto.InitialSetupDone.InitialSetupDoneDetails getDefaultInstanceForType() {
        return proto.InitialSetupDone.InitialSetupDoneDetails.getDefaultInstance();
      }

      @java.lang.Override
      public proto.InitialSetupDone.InitialSetupDoneDetails build() {
        proto.InitialSetupDone.InitialSetupDoneDetails result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public proto.InitialSetupDone.InitialSetupDoneDetails buildPartial() {
        proto.InitialSetupDone.InitialSetupDoneDetails result = new proto.InitialSetupDone.InitialSetupDoneDetails(this);
        int from_bitField0_ = bitField0_;
        result.messageId_ = messageId_;
        result.done_ = done_;
        result.currentOffSet_ = currentOffSet_;
        if (((bitField0_ & 0x00000001) != 0)) {
          topics_ = topics_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.topics_ = topics_;
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
        if (other instanceof proto.InitialSetupDone.InitialSetupDoneDetails) {
          return mergeFrom((proto.InitialSetupDone.InitialSetupDoneDetails)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(proto.InitialSetupDone.InitialSetupDoneDetails other) {
        if (other == proto.InitialSetupDone.InitialSetupDoneDetails.getDefaultInstance()) return this;
        if (other.getMessageId() != 0) {
          setMessageId(other.getMessageId());
        }
        if (other.getDone() != false) {
          setDone(other.getDone());
        }
        if (other.getCurrentOffSet() != 0L) {
          setCurrentOffSet(other.getCurrentOffSet());
        }
        if (!other.topics_.isEmpty()) {
          if (topics_.isEmpty()) {
            topics_ = other.topics_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTopicsIsMutable();
            topics_.addAll(other.topics_);
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
        proto.InitialSetupDone.InitialSetupDoneDetails parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (proto.InitialSetupDone.InitialSetupDoneDetails) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int messageId_ ;
      /**
       * <code>uint32 messageId = 1;</code>
       * @return The messageId.
       */
      @java.lang.Override
      public int getMessageId() {
        return messageId_;
      }
      /**
       * <code>uint32 messageId = 1;</code>
       * @param value The messageId to set.
       * @return This builder for chaining.
       */
      public Builder setMessageId(int value) {
        
        messageId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 messageId = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessageId() {
        
        messageId_ = 0;
        onChanged();
        return this;
      }

      private boolean done_ ;
      /**
       * <code>bool done = 2;</code>
       * @return The done.
       */
      @java.lang.Override
      public boolean getDone() {
        return done_;
      }
      /**
       * <code>bool done = 2;</code>
       * @param value The done to set.
       * @return This builder for chaining.
       */
      public Builder setDone(boolean value) {
        
        done_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bool done = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearDone() {
        
        done_ = false;
        onChanged();
        return this;
      }

      private long currentOffSet_ ;
      /**
       * <code>uint64 currentOffSet = 3;</code>
       * @return The currentOffSet.
       */
      @java.lang.Override
      public long getCurrentOffSet() {
        return currentOffSet_;
      }
      /**
       * <code>uint64 currentOffSet = 3;</code>
       * @param value The currentOffSet to set.
       * @return This builder for chaining.
       */
      public Builder setCurrentOffSet(long value) {
        
        currentOffSet_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint64 currentOffSet = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearCurrentOffSet() {
        
        currentOffSet_ = 0L;
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList topics_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTopicsIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          topics_ = new com.google.protobuf.LazyStringArrayList(topics_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string topics = 4;</code>
       * @return A list containing the topics.
       */
      public com.google.protobuf.ProtocolStringList
          getTopicsList() {
        return topics_.getUnmodifiableView();
      }
      /**
       * <code>repeated string topics = 4;</code>
       * @return The count of topics.
       */
      public int getTopicsCount() {
        return topics_.size();
      }
      /**
       * <code>repeated string topics = 4;</code>
       * @param index The index of the element to return.
       * @return The topics at the given index.
       */
      public java.lang.String getTopics(int index) {
        return topics_.get(index);
      }
      /**
       * <code>repeated string topics = 4;</code>
       * @param index The index of the value to return.
       * @return The bytes of the topics at the given index.
       */
      public com.google.protobuf.ByteString
          getTopicsBytes(int index) {
        return topics_.getByteString(index);
      }
      /**
       * <code>repeated string topics = 4;</code>
       * @param index The index to set the value at.
       * @param value The topics to set.
       * @return This builder for chaining.
       */
      public Builder setTopics(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTopicsIsMutable();
        topics_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string topics = 4;</code>
       * @param value The topics to add.
       * @return This builder for chaining.
       */
      public Builder addTopics(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTopicsIsMutable();
        topics_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string topics = 4;</code>
       * @param values The topics to add.
       * @return This builder for chaining.
       */
      public Builder addAllTopics(
          java.lang.Iterable<java.lang.String> values) {
        ensureTopicsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, topics_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string topics = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearTopics() {
        topics_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string topics = 4;</code>
       * @param value The bytes of the topics to add.
       * @return This builder for chaining.
       */
      public Builder addTopicsBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTopicsIsMutable();
        topics_.add(value);
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


      // @@protoc_insertion_point(builder_scope:tutorial.InitialSetupDoneDetails)
    }

    // @@protoc_insertion_point(class_scope:tutorial.InitialSetupDoneDetails)
    private static final proto.InitialSetupDone.InitialSetupDoneDetails DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new proto.InitialSetupDone.InitialSetupDoneDetails();
    }

    public static proto.InitialSetupDone.InitialSetupDoneDetails getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<InitialSetupDoneDetails>
        PARSER = new com.google.protobuf.AbstractParser<InitialSetupDoneDetails>() {
      @java.lang.Override
      public InitialSetupDoneDetails parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new InitialSetupDoneDetails(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<InitialSetupDoneDetails> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<InitialSetupDoneDetails> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public proto.InitialSetupDone.InitialSetupDoneDetails getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tutorial_InitialSetupDoneDetails_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tutorial_InitialSetupDoneDetails_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026InitialSetupDone.proto\022\010tutorial\"a\n\027In" +
      "itialSetupDoneDetails\022\021\n\tmessageId\030\001 \001(\r" +
      "\022\014\n\004done\030\002 \001(\010\022\025\n\rcurrentOffSet\030\003 \001(\004\022\016\n" +
      "\006topics\030\004 \003(\tB\031\n\005protoB\020InitialSetupDone" +
      "b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_tutorial_InitialSetupDoneDetails_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tutorial_InitialSetupDoneDetails_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tutorial_InitialSetupDoneDetails_descriptor,
        new java.lang.String[] { "MessageId", "Done", "CurrentOffSet", "Topics", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
