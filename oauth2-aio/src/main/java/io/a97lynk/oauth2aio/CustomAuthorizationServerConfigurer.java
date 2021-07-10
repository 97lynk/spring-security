package io.a97lynk.oauth2aio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Component
public class CustomAuthorizationServerConfigurer extends
		AuthorizationServerConfigurerAdapter {

	private final DataSource dataSource;

	@Autowired
	private AuthorizationEndpoint authorizationEndpoint;

	@PostConstruct
	public void init() {
		authorizationEndpoint.setUserApprovalPage("forward:/oauth/confirm");
//		authorizationEndpoint.setErrorPage("forward:/oauth/custom_error");
	}

	public CustomAuthorizationServerConfigurer(DataSource dataSource) throws Exception {
		this.dataSource = dataSource;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource)
				.passwordEncoder(NoOpPasswordEncoder.getInstance());
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
//		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter));

		endpoints
//				.tokenEnhancer(tokenEnhancerChain)
				.approvalStore(new JdbcApprovalStore(dataSource))
				.authorizationCodeServices(new JdbcAuthorizationCodeServices(dataSource));

		super.configure(endpoints);
	}
//
//	@Override
//	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
//		endpoints.authenticationManager(authenticationManager);
//	}
}