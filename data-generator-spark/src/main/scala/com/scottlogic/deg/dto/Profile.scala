package com.scottlogic.deg.dto

case class Profile(
  schemaVersion: String,
  fields: Seq[AbstractField]
)