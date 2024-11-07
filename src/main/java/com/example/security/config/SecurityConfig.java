package com.example.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.security.config.filter.JwtFilter;

@Configuration

/* Indica para o spring nao utilizar as configuracoes default
 * e sim as descritas nesse arquivo
 */
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtFilter jwtFilter;
	
	// Bean responsavel por definir filtros do FilterChain antes de acessar o conteudo da app
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		return http.csrf(customizer -> customizer.disable())
		// Define que qlqr endpoint precisa estar autenticado
			.authorizeHttpRequests(request -> request
					.requestMatchers("register", "login").permitAll()
					.anyRequest().authenticated()
			)
		// Define o login default do springSecurity
//		http.formLogin(Customizer.withDefaults());
		// Define o basico para clients como postman n receber o html
			.httpBasic(Customizer.withDefaults())
		/* Define q a sessao se torna STATELESS no navegador
		 * Evitando o problema dos tokens de sessao q causados 
		 * pelo csrf desabilitado
		 */
			.sessionManagement(session -> 
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			/* Define que primeiro o request passa pelo filtro 'jwtFilter' que
			 * valida se possui uma sessão ativa para depois executar o filtro 
			 * .class nesse caso UsernamePasswordAuthenticationToken
			 * */
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
		
	}
	
	/* Bean responsavel por definir e validar proprias regras de validacao de segurança 
	 * e retornar usuarios logados
	 */
//	@Bean
//	public UserDetailsService userDetailsService() {
//		
//		/* Criando nossa propria conf d usuario pois agr o Spring n utiliza mais as confs de acesso
//		 * default q podem ser configuradas via .yml
//		 */
//		UserDetails user1 = User
//				.withDefaultPasswordEncoder()
//				.username("teste")
//				.password("teste123")
//				.roles("USER")
//				.build();
//		
//		UserDetails user2 = User
//				.withDefaultPasswordEncoder()
//				.username("teste2")
//				.password("teste456")
//				.roles("ADMIN")
//				.build();
//		
//		return new InMemoryUserDetailsManager(user1, user2);
//	}
	
	// Provedor de auth que tambem valida info do BD
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		
		/*
		 * Define para o provedor que quando receber a senha para se autenticar
		 * o mesmo deve entao criptografar a mesma para seguir com a validacao
		 * */
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		provider.setUserDetailsService(userDetailsService);
		
		return provider;
	}
	
	/* Bean responsavel por lidar com processos de auth *
	 * - Conversa diretamente com o AuthenticationProvider
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
