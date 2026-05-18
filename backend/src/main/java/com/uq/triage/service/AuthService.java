package com.uq.triage.service;

import com.uq.triage.dto.LoginRequestDTO;
import com.uq.triage.dto.LoginResponseDTO;
import com.uq.triage.entity.Usuario;
import com.uq.triage.exception.CredencialesInvalidasException;
import com.uq.triage.repository.UsuarioRepository;
import com.uq.triage.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!usuario.isActivo())
            throw new CredencialesInvalidasException();

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword()))
            throw new CredencialesInvalidasException();

        String token = jwtUtil.generarToken(
                usuario.getCorreo(), usuario.getRol().name(), usuario.getId());

        return new LoginResponseDTO(
                token, usuario.getCorreo(),
                usuario.getNombreCompleto(), usuario.getRol().name(), usuario.getId());
    }
}
