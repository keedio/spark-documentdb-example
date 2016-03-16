package evo.insight

package object common {

  type ChannelId = String
  type ClientId = String
  type CommerceId = String
  type ContractId = String
  type Currency = String

  type DateTime = Long

  case class Category(Id: Long, Name: String)
}

