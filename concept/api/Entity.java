/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package grakn.client.concept.api;

import javax.annotation.CheckReturnValue;

/**
 * An instance of Entity Type EntityType
 * This represents an entity in the graph.
 * Entities are objects which are defined by their Attribute and their links to
 * other entities via Relation
 */
public interface Entity extends Thing {
    //------------------------------------- Accessors ----------------------------------

    /**
     * @return The EntityType of this Entity
     * @see EntityType
     */
    @Override
    EntityType type();

    /**
     * Creates a relation from this instance to the provided Attribute.
     *
     * @param attribute The Attribute to which a relation is created
     * @return The instance itself
     */
    @Override
    Entity has(Attribute attribute);

    /**
     * Removes the provided Attribute from this Entity
     *
     * @param attribute the Attribute to be removed
     * @return The Entity itself
     */
    @Override
    Entity unhas(Attribute attribute);

    //------------------------------------- Other ---------------------------------
    @Deprecated
    @CheckReturnValue
    @Override
    default Entity asEntity() {
        return this;
    }

    @Deprecated
    @CheckReturnValue
    @Override
    default boolean isEntity() {
        return true;
    }
}
