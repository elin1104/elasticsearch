/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.expression.predicate.regex;

import org.elasticsearch.xpack.sql.expression.LeafExpression;
import org.elasticsearch.xpack.sql.tree.Location;
import org.elasticsearch.xpack.sql.tree.NodeInfo;
import org.elasticsearch.xpack.sql.type.DataType;
import org.elasticsearch.xpack.sql.util.StringUtils;

import java.util.Objects;

/**
 * A SQL 'like' pattern.
 * Similar to basic regex, supporting '_' instead of '?' and '%' instead of '*'.
 * <p>
 * Allows escaping based on a regular char.
 *
 * To prevent conflicts with ES, the string and char must be validated to not contain '*'.
 */
public class LikePattern extends LeafExpression {

    private final String pattern;
    private final char escape;

    private final String regex;
    private final String wildcard;
    private final String indexNameWildcard;

    public LikePattern(Location location, String pattern, char escape) {
        super(location);
        this.pattern = pattern;
        this.escape = escape;
        // early initialization to force string validation
        this.regex = StringUtils.likeToJavaPattern(pattern, escape);
        this.wildcard = StringUtils.likeToLuceneWildcard(pattern, escape);
        this.indexNameWildcard = StringUtils.likeToIndexWildcard(pattern, escape);
    }

    @Override
    protected NodeInfo<LikePattern> info() {
        return NodeInfo.create(this, LikePattern::new, pattern, escape);
    }

    public String pattern() {
        return pattern;
    }

    public char escape() {
        return escape;
    }

    /**
     * Returns the pattern in (Java) regex format.
     */
    public String asJavaRegex() {
        return regex;
    }

    /**
     * Returns the pattern in (Lucene) wildcard format.
     */
    public String asLuceneWildcard() {
        return wildcard;
    }

    /**
     * Returns the pattern in (IndexNameExpressionResolver) wildcard format.
     */
    public String asIndexNameWildcard() {
        return indexNameWildcard;
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public DataType dataType() {
        return DataType.KEYWORD;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, escape);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        LikePattern other = (LikePattern) obj;
        return Objects.equals(pattern, other.pattern)
                && escape == other.escape;
    }
}