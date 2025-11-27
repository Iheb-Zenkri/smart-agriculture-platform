package com.smartagri.recommendation.config;
import graphql.language.StringValue;
import graphql.schema.*;

import java.math.BigDecimal;

public class DecimalScalar {

    public static final GraphQLScalarType DECIMAL = GraphQLScalarType.newScalar()
            .name("Decimal")
            .description("A custom scalar to handle Decimal values")
            .coercing(new Coercing<BigDecimal, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    return ((BigDecimal) dataFetcherResult).toString();
                }

                @Override
                public BigDecimal parseValue(Object input) {
                    return new BigDecimal(input.toString());
                }

                @Override
                public BigDecimal parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        return new BigDecimal(((StringValue) input).getValue());
                    }
                    return null;
                }
            })
            .build();
}
