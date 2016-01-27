package jp.o3co.file.store.naming

import jp.o3co.file.store.meta.Meta
/**
 *
 */
case class PatternNaming(pattern: String) extends Naming {
  /**
   *
   */
  def apply(meta: Meta): String = {
    pattern
      .replaceAll("\\{id\\}", meta.id.toString)
      .replaceAll("\\{extension\\}", meta.contentType.toFileExtension.toString)
      .replaceAll("\\{name\\}", meta.name.toString)
      .replaceAll("\\{created\\}", meta.created.toString)
      .replaceAll("\\{updated\\}", meta.updated.toString)
  }
}
