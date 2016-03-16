package evo.insight.common

case class Payment(uuid: String, cif: String, nuc: ContractId, date: DateTime, amount: Double,
                   textDescription: String, category: Category) extends IdTrait
