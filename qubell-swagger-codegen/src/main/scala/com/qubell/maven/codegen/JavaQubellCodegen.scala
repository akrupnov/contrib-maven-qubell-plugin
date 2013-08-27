import com.wordnik.swagger.codegen.BasicJavaGenerator

/*
 * Copyright 2013 Qubell, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qubell.maven.codegen {

object JavaQubellCodegen extends BasicJavaGenerator {
  override def defaultIncludes = Set(
    "double",
    "int",
    "long",
    "float",
    "String",
    "boolean",
    "object",
    "Object",
    "Boolean",
    "Double",
    "Integer",
    "Long",
    "Float")

  def main(args: Array[String]) = generateClient(args)

  // location of templates
  override def templateDir = System.getProperty("mustache.template.dir")

  // where to write generated code
  override def destinationDir = System.getProperty("swagger.codegen.destination")

  // package for api invoker, error files
  override def invokerPackage = Some(System.getProperty("swagger.codegen.base.package"))

  // package for models
  override def modelPackage = Some(System.getProperty("swagger.codegen.base.package") + ".model")

  // package for api classes
  override def apiPackage = Some(System.getProperty("swagger.codegen.base.package") + ".api")

  override def toGetter(name: String, datatype: String) = {
    val base = datatype match {
      case "boolean" => "is"
      case _ => "get"
    }
    val varName = toVarName(name);

    base + varName(0).toUpper + varName.substring(1)
  }

  override def toSetter(name: String, datatype: String) = {
    val base = datatype match {
      case _ => "set"
    }

    val varName = toVarName(name);

    base + varName(0).toUpper + varName.substring(1)
  }

  override def toVarName(name: String): String = toCamelCase(name)

  def toCamelCase(name: String): String = {
    val sb = new StringBuilder

    val parts = name.split(Array('-'))

    if (parts.size == 1) {
      parts(0)
    } else {
      sb.append(parts(0))

      for (i <- 1 to parts.size - 1) {
        sb.append(parts(i).capitalize)
      }

      sb.toString()
    }
  }


  // supporting classes
  override def supportingFiles =
    List(
      ("apiInvoker.mustache", destinationDir + java.io.File.separator + invokerPackage.get.replace(".", java.io.File.separator) + java.io.File.separator, "ApiInvoker.java"),
      ("JsonUtil.mustache", destinationDir + java.io.File.separator + invokerPackage.get.replace(".", java.io.File.separator) + java.io.File.separator, "JsonUtil.java"),
      ("apiException.mustache", destinationDir + java.io.File.separator + invokerPackage.get.replace(".", java.io.File.separator) + java.io.File.separator, "ApiException.java"))
}

}