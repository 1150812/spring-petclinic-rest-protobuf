// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: resources/petclinic.proto

// Protobuf Java Version: 3.25.3
package org.springframework.samples.petclinic.protobuf;

public interface ProtoPetAddOrBuilder extends
    // @@protoc_insertion_point(interface_extends:org.springframework.samples.petclinic.protobuf.ProtoPetAdd)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string name = 1;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <code>string name = 1;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <code>string birth_date = 2;</code>
   * @return The birthDate.
   */
  java.lang.String getBirthDate();
  /**
   * <code>string birth_date = 2;</code>
   * @return The bytes for birthDate.
   */
  com.google.protobuf.ByteString
      getBirthDateBytes();

  /**
   * <code>int32 pet_type_id = 3;</code>
   * @return The petTypeId.
   */
  int getPetTypeId();

  /**
   * <code>int32 owner_id = 4;</code>
   * @return The ownerId.
   */
  int getOwnerId();
}
