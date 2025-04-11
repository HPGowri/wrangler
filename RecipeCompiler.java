/*
 * Copyright © 2024 Cask Data, Inc.
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

package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.TokenGroup;
import io.cdap.wrangler.utils.ByteSize;
import io.cdap.wrangler.utils.TimeDuration;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import java.util.ArrayList;
import java.util.List;

public class RecipeCompiler extends DirectivesBaseVisitor<Object> {

  @Override
  public Object visitRecipe(DirectivesParser.RecipeContext ctx) {
    List<Object> steps = new ArrayList<>();
    for (DirectivesParser.StatementContext stmt : ctx.statement()) {
      steps.add(visit(stmt));
    }
    return steps;
  }

  @Override
  public Object visitValue(DirectivesParser.ValueContext ctx) {
    if (ctx.BYTE_SIZE() != null) {
      return new ByteSize(ctx.BYTE_SIZE().getText());
    } else if (ctx.TIME_DURATION() != null) {
      return new TimeDuration(ctx.TIME_DURATION().getText());
    } else if (ctx.STRING() != null) {
      return stripQuotes(ctx.STRING().getText());
    } else if (ctx.BOOLEAN() != null) {
      return Boolean.parseBoolean(ctx.BOOLEAN().getText());
    } else if (ctx.NUMBER() != null) {
      return Double.parseDouble(ctx.NUMBER().getText());
    }
    return null;
  }

  private String stripQuotes(String text) {
    if (text == null) return null;
    return text.replaceAll("^['\"]|['\"]$", "");
  }

  @Override
  public Object visitTokenArg(DirectivesParser.TokenArgContext ctx) {
    return visit(ctx.value());
  }

  @Override
  public Object visitTokenGroup(DirectivesParser.TokenGroupContext ctx) {
    TokenGroup group = new TokenGroup();
    for (DirectivesParser.TokenArgContext arg : ctx.tokenArg()) {
      group.add(visit(arg));
    }
    return group;
  }
}

