package guru.sfg.brewery.config;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import guru.sfg.brewery.security.RestUrlParamAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /* Adding the PasswordEncoder bean overrides the default implementation of the delegating
    * password encoder. This encoder stores passwords in plaintext which is insecure.*/
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new LdapShaPasswordEncoder();
//    }

//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new StandardPasswordEncoder();
//    }

//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    public RestUrlParamAuthFilter urlParamAuthFilter(AuthenticationManager authenticationManager) {
        RestUrlParamAuthFilter filter = new RestUrlParamAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                    UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(urlParamAuthFilter(authenticationManager()),
                        RestHeaderAuthFilter.class)
                .csrf().disable();

        http
                .authorizeRequests(authorize -> {
                    authorize
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .antMatchers("/beers/find", "/beers*").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                            .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll();
                })
                .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                .and()
                    .formLogin()
                .and()
                    .httpBasic();
    }

// Fluent API way of overriding user configuration
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("{bcrypt}$2a$10$4hnwRUnZs1HdIPCz0FF9vOI9eMPNPbrhxESzVSrBKHWlyblNg8XZC")
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{sha256}eacda355d020fb6d58ef7db5dabee00c7b99955eddfe2ceef4cad923714700e9114798111d65597a")
                .roles("USER")
                .and()
                .withUser("scott")
                .password("{ldap}{SSHA}x/FxXFJsPiMmss8iFg5pSCDuyR++tNvgpKLxtw==")
                .roles("CUSTOMER");


    }

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("spring")
//                .password("guru")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }
}
