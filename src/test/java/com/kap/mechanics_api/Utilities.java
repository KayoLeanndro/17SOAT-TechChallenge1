package com.kap.mechanics_api;

import com.kap.mechanics_api.dto.veiculo.*;

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


}
