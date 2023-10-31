package com.intuit.graphql.example.nameservice.auth.config;

import com.intuit.graphql.authorization.config.AuthzClient;
import com.intuit.graphql.authorization.config.AuthzClientConfiguration;
import com.intuit.graphql.authorization.enforcement.AuthzInstrumentation;
import com.intuit.graphql.authorization.enforcement.RedactionContext;
import com.intuit.graphql.authorization.util.ScopeProvider;
import com.intuit.graphql.example.nameservice.utils.LoadDataForAuthClient;
import com.netflix.graphql.dgs.context.DgsContext;
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData;
import graphql.schema.GraphQLSchema;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.*;

@Configuration
@ConditionalOnProperty(name="authz.instrumentation.enabled", havingValue = "true")
public class NameServiceAuthConfigImplementation {

    //Queries By Client
    public static Map<AuthzClient, List<String>> queriesByClient = new HashMap<>();

    // Permissions loaded from yaml files
    static {

        try {
            AuthzClient user1 = LoadDataForAuthClient
                    .yamlMapper().readValue(LoadDataForAuthClient.read("authorization.userpermissions/userId1.yml"), AuthzClient.class);
            AuthzClient user2 = LoadDataForAuthClient
                    .yamlMapper().readValue(LoadDataForAuthClient.read("authorization.userpermissions/userId2.yml"), AuthzClient.class);
            AuthzClient user3 = LoadDataForAuthClient
                    .yamlMapper().readValue(LoadDataForAuthClient.read("authorization.userpermissions/userId3.yml"), AuthzClient.class);

            queriesByClient.put(user1, Collections.singletonList(
                    LoadDataForAuthClient.readString("authorization.userpermissions/userId1-permissions.graphql")
            ));
            queriesByClient.put(user2, Collections.singletonList(
                    LoadDataForAuthClient.readString("authorization.userpermissions/userId2-permissions.graphql")
            ));
            queriesByClient.put(user3, Collections.singletonList(
                    LoadDataForAuthClient.readString("authorization.userpermissions/userId3-permissions.graphql")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public ScopeProvider getScopeProvider() {
        return new ScopeProvider() {
            @Override
            public Set<String> getScopes(Object o) {
                DgsContext dgsContext = (DgsContext) o;
                Object localContext = dgsContext.getCustomContext();
                HttpHeaders headers = ((DgsWebMvcRequestData) localContext).getHeaders();
                String userId = headers.get("x-userid").get(0);
                HashSet<String> scopes = new HashSet();
                scopes.add(userId);
                return scopes;
            }

            @Override
            public String getErrorMessage(RedactionContext redactionContext) {
                String OVERRIDDEN_ERROR_MESSAGE = "You are not authorized to access field=%s of type=%s";
                return String.format(OVERRIDDEN_ERROR_MESSAGE,
                        redactionContext.getField().getName(), redactionContext.getFieldCoordinates().getTypeName());
            }
        };
    }

    @Bean
    public AuthzInstrumentation getAuthZInstrumentation(AuthzClientConfiguration authzConfig, GraphQLSchema schema, ScopeProvider scopeProvider) {
        return new AuthzInstrumentation(authzConfig, schema, scopeProvider, null, null);
    }

    @Bean
    public AuthzClientConfiguration getAuthZClientConfiguration() {
        return new AuthzClientConfiguration() {
            @Override
            public Map<AuthzClient, List<String>> getQueriesByClient() {
                return queriesByClient;
            }
        };
    }
}
