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

import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveContext;
import io.cdap.wrangler.api.RecipeParser;
import io.cdap.wrangler.api.RecipeException;
import io.cdap.wrangler.api.parser.DirectiveInfo;
import io.cdap.wrangler.api.parser.DirectiveRegistry;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GrammarBasedParser implements RecipeParser {
  private static final char EOL = '\n';
  private final String namespace;
  private final DirectiveRegistry registry;
  private final String recipe;
  private final DirectiveContext context;

  public GrammarBasedParser(String namespace, String recipe, DirectiveRegistry registry) {
    this(namespace, recipe, registry, new NoOpDirectiveContext());
  }

  public GrammarBasedParser(String namespace, String[] directives,
                            DirectiveRegistry registry, DirectiveContext context) {
    this(namespace, String.join(String.valueOf(EOL), directives), registry, context);
  }

  public GrammarBasedParser(String namespace, String recipe, DirectiveRegistry registry, DirectiveContext context) {
    this.namespace = namespace;
    this.recipe = recipe;
    this.registry = registry;
    this.context = context;
  }

  @Override
  public List<Directive> parse() throws RecipeException {
    AtomicInteger directiveIndex = new AtomicInteger();
    try {
      List<Directive> result = new ArrayList<>();

      new GrammarWalker(new RecipeCompiler(), context).walk(recipe, (command, tokenGroup) -> {
        directiveIndex.getAndIncrement();
        DirectiveInfo info = registry.get(namespace, command);
        if (info == null) {
          throw new RecipeException(
            String.format("Directive '%s' not found in system and user scope. Check the name of directive.", command),
            directiveIndex.get()
          );
        }

        Directive directive = info.instance();
        UsageDefinition definition = directive.define();
        directive.initialize(new MapArguments(definition, tokenGroup));
        result.add(directive);
      });

      return result;
    } catch (Exception e) {
      throw new RecipeException(e.getMessage(), e, directiveIndex.get());
    }
  }
}

