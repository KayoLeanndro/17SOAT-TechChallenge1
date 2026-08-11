package com.kap.mechanics_api;

import com.kap.mechanics_api.dto.usuario.*;
import com.kap.mechanics_api.dto.veiculo.*;
import com.kap.mechanics_api.enums.TipoUsuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Utilities {

    public static List<ListagemVeiculoResponseDTO>  produzirListaVeiculosResponse(){
        List<ListagemVeiculoResponseDTO> veiculosDto = new ArrayList<>();
        ListagemVeiculoResponseDTO dto1 = new ListagemVeiculoResponseDTO(1, "ABC1234", "Honda", "Civic", 2020);
        ListagemVeiculoResponseDTO dto2 = new ListagemVeiculoResponseDTO(2, "ADD1234", "Honda", "CRV", 2022);
        veiculosDto.add(dto1);
        veiculosDto.add(dto2);

        return veiculosDto;
    }

    public static ListagemVeiculoResponseDTO produzirRespostaListagemVeiculoDto(){
        return new ListagemVeiculoResponseDTO(1, "ABC1234", "Honda", "Civic", 2020);
    }

    public static CriacaoVeiculoResponseDTO produzirVeiculoSalvoDto(){
        return new CriacaoVeiculoResponseDTO(1, "ABC1234", "Honda", "Civic", 2020);
    }

    public static CriacaoVeiculoRequestDTO produzirVeiculoRequestDto(){
        return new CriacaoVeiculoRequestDTO("ABC1234", "Honda", "Civic", 2020);
    }

    public static AtualizacaoVeiculoRequestDTO produzirAtualizacaoVeiculoDto(){
        return new AtualizacaoVeiculoRequestDTO("ABC1234", "Honda2", "Civic2", 2020);
    }

    public static AtualizacaoVeiculoRequestDTO produzirAtualizacaoVeiculoDtoInvalido(){
        return new AtualizacaoVeiculoRequestDTO("", "", "", null);
    }

    public static AtualizacaoVeiculoResponseDTO produzirAtualizacaoVeiculoResponseDto(){
        return new AtualizacaoVeiculoResponseDTO(1, "ABC1234", "Honda2", "Civic2", 2020);
    }

    public static List<ListagemUsuarioDTO> produzirListaUsuariosResponse() {
        List<ListagemUsuarioDTO> usuariosDto = new ArrayList<>();
        ListagemUsuarioDTO dto1 = new ListagemUsuarioDTO( 1, "pedin", "1234", "pedinApelao", LocalDateTime.now(), TipoUsuario.ATENDENTE );
        ListagemUsuarioDTO dto2 = new ListagemUsuarioDTO( 2, "joao", "1234", "joaoApelao", LocalDateTime.now(), TipoUsuario.ATENDENTE );
        usuariosDto.add(dto1);
        usuariosDto.add(dto2);
        return usuariosDto;
    }
    public static ListagemUsuarioDTO produzirRespostaListagemUsuarioDto() {
        return new ListagemUsuarioDTO( 1, "pedin", "1234", "pedinApelao", LocalDateTime.now(), TipoUsuario.ATENDENTE );
    }
    public static CriacaoUsuarioResponseDTO produzirUsuarioSalvoDto() {
        return new CriacaoUsuarioResponseDTO( 1, "pedin", "pedinApelao", LocalDateTime.now(), TipoUsuario.ATENDENTE );
    }
    public static CriacaoUsuarioRequestDTO produzirUsuarioRequestDto() {
        return new CriacaoUsuarioRequestDTO( "pedin", "pedinApelao", "1234", TipoUsuario.ATENDENTE.toString() );
    }
    public static AtualizacaoUsuarioRequestDTO produzirAtualizacaoUsuarioDto() {
        return new AtualizacaoUsuarioRequestDTO( "pedin2", "pedinApelao2", "12345", "ATENDENTE" );
    }
    public static AtualizacaoUsuarioRequestDTO produzirAtualizacaoUsuarioDtoInvalido() {
        return new AtualizacaoUsuarioRequestDTO( "", "", "", "" );
    }
    public static AtualizacaoUsuarioResponseDTO produzirAtualizacaoUsuarioResponseDto() {
        return new AtualizacaoUsuarioResponseDTO( 1, "pedin2", "pedinApelao2", "12345", TipoUsuario.ATENDENTE );
    }


}
