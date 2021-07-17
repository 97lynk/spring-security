package io.a97lynk.oauth2aio;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer

public class CustomAuthorizationServerConfigurer extends OAuth2AuthorizationServerConfiguration {

	private final DataSource dataSource;

	private JwtAccessTokenConverter jwtAccessTokenConverter;

	public CustomAuthorizationServerConfigurer(BaseClientDetails details, AuthenticationConfiguration authenticationConfiguration,
	                                           ObjectProvider<TokenStore> tokenStore, ObjectProvider<AccessTokenConverter> tokenConverter,
	                                           AuthorizationServerProperties properties, DataSource dataSource,
	                                           JwtAccessTokenConverter jwtAccessTokenConverter) throws Exception {
		super(details, authenticationConfiguration, tokenStore, tokenConverter, properties);
		this.dataSource = dataSource;
		this.jwtAccessTokenConverter = jwtAccessTokenConverter;
	}


	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer
				.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource)
				.passwordEncoder(NoOpPasswordEncoder.getInstance());
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter));

		endpoints
//				.accessTokenConverter(jwtAccessTokenConverter)
				.tokenStore(new JdbcTokenStore(dataSource))
				.tokenEnhancer(tokenEnhancerChain)
				.approvalStore(new JdbcApprovalStore(dataSource))
				.authorizationCodeServices(new JdbcAuthorizationCodeServices(dataSource))
				.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);

		super.configure(endpoints);
	}
}