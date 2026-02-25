package com.example.lotogestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.lotogestor.usuario.Usuario;
import com.example.lotogestor.usuario.UsuarioRepository;
import com.example.lotogestor.usuario.UsuarioRole;
import com.example.lotogestor.usuario.UsuarioService;

@SpringBootApplication
public class LotoGestorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LotoGestorApplication.class, args);
	}

	@Bean
	CommandLineRunner seedAdmin(UsuarioRepository usuarios, UsuarioService usuarioService) {
		return args -> {
			if (!usuarios.existsByEmail("admin@lotogestor.com")) {
				Usuario admin = new Usuario();
				admin.setNomeCompleto("Administrador");
				admin.setEmail("admin@lotogestor.com");
				admin.setEndereco("Nao informado");
				admin.setCpf("00000000000");
				admin.setRole(UsuarioRole.ADMIN);
				admin.setFotoUrl(null);
				usuarioService.criarUsuario(admin, "123456");
			}
		};
	}

}
