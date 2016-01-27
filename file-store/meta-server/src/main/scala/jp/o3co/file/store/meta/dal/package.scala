package jp.o3co.file.store
package meta 


package object dal {

  case class ContainsEntity(id: ResourceId)

  case class ContainsEntityResult(isContains: Boolean)

  case class PutEntity(meta: Meta)

  case class PutEntityResult()
}
