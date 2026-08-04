package com.kap.mechanics_api.veiculo;

import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.mapper.VeiculoMapper;
import com.kap.mechanics_api.repository.VeiculoRepository;
import com.kap.mechanics_api.service.VeiculoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.kap.mechanics_api.dto.veiculo.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private VeiculoMapper veiculoMapper;

    @InjectMocks
    private VeiculoService veiculoService;


    @Test
    void deveCriarVeiculoComSucesso(){

        //Arrange
        CriacaoVeiculoRequestDTO veiculoDto = new CriacaoVeiculoRequestDTO("ABC1234", "Honda", "Civic", 2020);
        Veiculo veiculo = new Veiculo(null, "ABC1234", "Honda", "Civic", 2020);
        Veiculo veiculoSalvo = new Veiculo(1, "ABC1234", "Honda", "Civic", 2020);
        CriacaoVeiculoResponseDTO veiculoSalvoDto = new CriacaoVeiculoResponseDTO(1, "ABC1234", "Honda", "Civic", 2020);

        when(veiculoMapper.toEntity(veiculoDto)).thenReturn(veiculo);
        when(veiculoMapper.toResponseDto(veiculoSalvo)).thenReturn(veiculoSalvoDto);
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculoSalvo);

        //act
        CriacaoVeiculoResponseDTO resultado = veiculoService.cadastrar(veiculoDto);

        //Assert
        assertNotNull(resultado);
        assertEquals("ABC1234", resultado.placa());
    }

}
