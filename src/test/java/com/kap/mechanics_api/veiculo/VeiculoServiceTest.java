package com.kap.mechanics_api.veiculo;

import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.domain.ClienteVeiculo;
import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.exception.ClienteNaoEncontradoException;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.VeiculoNaoEncontradoException;
import com.kap.mechanics_api.mapper.VeiculoMapper;
import com.kap.mechanics_api.repository.ClienteVeiculoRepository;
import com.kap.mechanics_api.repository.VeiculoRepository;
import com.kap.mechanics_api.service.ClienteService;
import com.kap.mechanics_api.service.VeiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.kap.mechanics_api.dto.veiculo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private VeiculoMapper veiculoMapper;

    @Mock
    private ClienteVeiculoRepository clienteVeiculoRepository;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private VeiculoService veiculoService;


    @Test
    void deveCadastrarVeiculoComSucesso() {

        // arrange
        CriacaoVeiculoRequestDTO dto =
                new CriacaoVeiculoRequestDTO(
                        "ABC1234",
                        "Honda",
                        "Civic",
                        2020,
                        1
                );

        Veiculo veiculo =
                new Veiculo(
                        1,
                        "ABC1234",
                        "Honda",
                        "Civic",
                        2020
                );

        Cliente cliente = new Cliente();
        cliente.setId(1);

        ClienteVeiculo clienteVeiculo =
                new ClienteVeiculo(veiculo, cliente);

        CriacaoVeiculoResponseDTO respostaEsperada =
                new CriacaoVeiculoResponseDTO(
                        1,
                        "ABC1234",
                        "Honda",
                        "Civic",
                        2020
                );

        when(veiculoMapper.toEntity(dto))
                .thenReturn(veiculo);

        when(veiculoRepository.save(veiculo))
                .thenReturn(veiculo);

        when(clienteService.pesquisarPorId(1))
                .thenReturn(cliente);

        when(clienteVeiculoRepository.save(any(ClienteVeiculo.class)))
                .thenReturn(clienteVeiculo);

        when(veiculoMapper.toResponseDto(veiculo))
                .thenReturn(respostaEsperada);

        // act
        CriacaoVeiculoResponseDTO resposta =
                veiculoService.cadastrar(dto);

        // assert
        assertNotNull(resposta);
        assertEquals(1, resposta.id());
        assertEquals("ABC1234", resposta.placa());
        assertEquals("Honda", resposta.marca());
        assertEquals("Civic", resposta.modelo());
        assertEquals(2020, resposta.ano());

        verify(veiculoMapper).toEntity(dto);
        verify(veiculoRepository).save(veiculo);

        verify(clienteService).pesquisarPorId(1);

        verify(clienteVeiculoRepository)
                .save(any(ClienteVeiculo.class));

        verify(veiculoMapper).toResponseDto(veiculo);
    }

    @Test
    void deveListarTodosOsVeiculos(){

        //Arrange
        Veiculo veiculoSalvo = new Veiculo(1, "ABC1234", "Honda", "Civic", 2020);
        Veiculo veiculoSalvo2 = new Veiculo(2, "ADD1234", "Honda", "CRV", 2022);
        List<Veiculo> veiculos = new ArrayList<>();
        veiculos.add(veiculoSalvo);
        veiculos.add(veiculoSalvo2);

        List<ListagemVeiculoResponseDTO> veiculosDto = new ArrayList<>();
        ListagemVeiculoResponseDTO dto1 = new ListagemVeiculoResponseDTO(1, "ABC1234", "Honda", "Civic", 2020,1);
        ListagemVeiculoResponseDTO dto2 = new ListagemVeiculoResponseDTO(2, "ADD1234", "Honda", "CRV", 2022,1);
        veiculosDto.add(dto1);
        veiculosDto.add(dto2);

        when(veiculoRepository.findAll()).thenReturn(veiculos);
        when(veiculoMapper.toListagemDto(veiculos)).thenReturn(veiculosDto);


        //act
        List<ListagemVeiculoResponseDTO> resultado = veiculoService.listar();

        //Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(1, resultado.get(0).id());
        assertEquals("ABC1234", resultado.get(0).placa());
        assertEquals("Honda", resultado.get(0).marca());
        assertEquals("Civic", resultado.get(0).modelo());
        assertEquals(2020, resultado.get(0).ano());

        assertEquals(2, resultado.get(1).id());
        assertEquals("ADD1234", resultado.get(1).placa());

        verify(veiculoRepository).findAll();
        verify(veiculoMapper).toListagemDto(veiculos);

    }


    @Test
    void deveBuscarVeiculoPorId(){

        //arrange
        Optional<Veiculo> veiculo = Optional.of(new Veiculo(1, "ABC1234", "Honda", "Civic", 2020));
        ListagemVeiculoResponseDTO dto1 = new ListagemVeiculoResponseDTO(1, "ABC1234", "Honda", "Civic", 2020, 1);

        when(veiculoRepository.findById(1)).thenReturn(veiculo);
        when(veiculoMapper.toListagemVeiculoResponseDto(veiculo.get())).thenReturn(dto1);

        //act
        ListagemVeiculoResponseDTO resultado = veiculoService.buscarPorId(veiculo.get().getId());

        //assert
        assertNotNull(resultado);
        assertEquals(1, resultado.id());
        assertEquals("ABC1234", resultado.placa());
        assertEquals("Honda", resultado.marca());
        assertEquals("Civic", resultado.modelo());
        assertEquals(2020, resultado.ano());

        verify(veiculoRepository).findById(1);
        verify(veiculoMapper).toListagemVeiculoResponseDto(veiculo.get());

    }

    @Test
    void deveDeletarVeiculoComSucesso(){

        //arrange
        Veiculo veiculo = new Veiculo(1, "ABC1234", "Honda", "Civic", 2020);
        when(veiculoRepository.findById(1)).thenReturn(Optional.of(veiculo));

        //act
        veiculoService.deletar(veiculo.getId());

        //assert
        verify(veiculoRepository).findById(1);
        verify(veiculoRepository).delete(veiculo);

    }

    @Test
    void deveAtualizarVeiculoComSucesso() {

        // arrange
        Veiculo veiculo =
                new Veiculo(
                        1,
                        "ABC1234",
                        "Honda",
                        "Civic",
                        2020
                );

        Cliente cliente = new Cliente();
        cliente.setId(1);

        AtualizacaoVeiculoRequestDTO dto =
                new AtualizacaoVeiculoRequestDTO(
                        "ABC1234",
                        "Honda2",
                        "Civic2",
                        2020,
                        1
                );

        AtualizacaoVeiculoResponseDTO dtoResposta =
                new AtualizacaoVeiculoResponseDTO(
                        1,
                        "ABC1234",
                        "Honda2",
                        "Civic2",
                        2020,
                        1
                );

        when(veiculoRepository.findById(1))
                .thenReturn(Optional.of(veiculo));

        when(veiculoRepository.save(veiculo))
                .thenReturn(veiculo);

        when(clienteService.pesquisarPorId(1))
                .thenReturn(cliente);

        when(clienteVeiculoRepository.save(any(ClienteVeiculo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(veiculoMapper.toAtualizacaoVeiculoResponseDto(veiculo))
                .thenReturn(dtoResposta);

        // act
        AtualizacaoVeiculoResponseDTO resposta =
                veiculoService.atualizar(dto, 1);

        // assert
        assertNotNull(resposta);

        assertEquals(1, resposta.id());
        assertEquals("ABC1234", resposta.placa());
        assertEquals("Honda2", resposta.marca());
        assertEquals("Civic2", resposta.modelo());
        assertEquals(2020, resposta.ano());
        assertEquals(1, resposta.clienteId());

        assertEquals("Honda2", veiculo.getMarca());
        assertEquals("Civic2", veiculo.getModelo());

        verify(veiculoRepository).findById(1);
        verify(veiculoRepository).save(veiculo);

        verify(clienteService).pesquisarPorId(1);

        verify(clienteVeiculoRepository)
                .save(any(ClienteVeiculo.class));

        verify(veiculoMapper)
                .toAtualizacaoVeiculoResponseDto(veiculo);
    }

    @Test
    void deveRetornarExceptionCasoVeiculoNaoExista() {

        // arrange
        when(veiculoRepository.findById(1))
                .thenReturn(Optional.empty());

        // act
        VeiculoNaoEncontradoException exception =
                assertThrows(
                        VeiculoNaoEncontradoException.class,
                        () -> veiculoService.buscarPorId(1)
                );

        // assert
        assertEquals(
                "Veiculo nao encontrado com o id 1",
                exception.getMessage()
        );

        verify(veiculoRepository).findById(1);
    }

    @Test
    void deveRetornarExceptionQuandoNaoInformadoNenhumParametroParaAtualizacao() {

        // arrange
        AtualizacaoVeiculoRequestDTO dto =
                new AtualizacaoVeiculoRequestDTO(
                        "",
                        "",
                        "",
                        null,
                        null
                );

        // act
        NenhumCampoInformadoException exception =
                assertThrows(
                        NenhumCampoInformadoException.class,
                        () -> veiculoService.atualizar(dto, 1)
                );

        // assert
        assertEquals(
                "Nenhum campo foi informado para atualização",
                exception.getMessage()
        );

        verifyNoInteractions(
                veiculoRepository,
                clienteService,
                clienteVeiculoRepository
        );
    }

    @Test
    void devePersistirVinculoEntreClienteEVeiculoAoCadastrar() {

        // arrange
        CriacaoVeiculoRequestDTO dto =
                new CriacaoVeiculoRequestDTO(
                        "ABC1234",
                        "Honda",
                        "Civic",
                        2020,
                        1
                );

        Veiculo veiculo =
                new Veiculo(
                        1,
                        "ABC1234",
                        "Honda",
                        "Civic",
                        2020
                );

        Cliente cliente = new Cliente();
        cliente.setId(1);

        when(veiculoMapper.toEntity(dto))
                .thenReturn(veiculo);

        when(veiculoRepository.save(veiculo))
                .thenReturn(veiculo);

        when(clienteService.pesquisarPorId(1))
                .thenReturn(cliente);

        when(veiculoMapper.toResponseDto(veiculo))
                .thenReturn(
                        new CriacaoVeiculoResponseDTO(
                                1,
                                "ABC1234",
                                "Honda",
                                "Civic",
                                2020
                        )
                );

        // act
        veiculoService.cadastrar(dto);

        // assert
        ArgumentCaptor<ClienteVeiculo> captor =
                ArgumentCaptor.forClass(ClienteVeiculo.class);

        verify(clienteVeiculoRepository)
                .save(captor.capture());

        ClienteVeiculo vinculo = captor.getValue();

        assertEquals(veiculo, vinculo.getVeiculo());
        assertEquals(cliente, vinculo.getCliente());
    }

    @Test
    void deveAtualizarVeiculoSemAlterarClienteQuandoClienteIdNaoForInformado() {

        // arrange
        Veiculo veiculo =
                new Veiculo(
                        1,
                        "ABC1234",
                        "Honda",
                        "Civic",
                        2020
                );

        AtualizacaoVeiculoRequestDTO dto =
                new AtualizacaoVeiculoRequestDTO(
                        null,
                        "Honda2",
                        null,
                        null,
                        null
                );

        AtualizacaoVeiculoResponseDTO dtoResposta =
                new AtualizacaoVeiculoResponseDTO(
                        1,
                        "ABC1234",
                        "Honda2",
                        "Civic",
                        2020,
                        null
                );

        when(veiculoRepository.findById(1))
                .thenReturn(Optional.of(veiculo));

        when(veiculoRepository.save(veiculo))
                .thenReturn(veiculo);

        when(veiculoMapper.toAtualizacaoVeiculoResponseDto(veiculo))
                .thenReturn(dtoResposta);

        // act
        AtualizacaoVeiculoResponseDTO resposta =
                veiculoService.atualizar(dto, 1);

        // assert
        assertNotNull(resposta);
        assertEquals(1, resposta.id());
        assertEquals("Honda2", resposta.marca());

        verify(veiculoRepository).findById(1);
        verify(veiculoRepository).save(veiculo);

        verify(veiculoMapper)
                .toAtualizacaoVeiculoResponseDto(veiculo);

        verifyNoInteractions(clienteService);
        verifyNoInteractions(clienteVeiculoRepository);
    }

    @Test
    void deveRetornarExceptionQuandoClienteNaoExistirNaAtualizacao() {

        // arrange
        Veiculo veiculo =
                new Veiculo(
                        1,
                        "ABC1234",
                        "Honda",
                        "Civic",
                        2020
                );

        AtualizacaoVeiculoRequestDTO dto =
                new AtualizacaoVeiculoRequestDTO(
                        null,
                        "Honda2",
                        null,
                        null,
                        999
                );

        when(veiculoRepository.findById(1))
                .thenReturn(Optional.of(veiculo));

        when(veiculoRepository.save(veiculo))
                .thenReturn(veiculo);

        when(clienteService.pesquisarPorId(999))
                .thenThrow(
                        new ClienteNaoEncontradoException(999)
                );

        // act + assert
        assertThrows(
                ClienteNaoEncontradoException.class,
                () -> veiculoService.atualizar(dto, 1)
        );

        verify(veiculoRepository).findById(1);
        verify(veiculoRepository).save(veiculo);

        verify(clienteService).pesquisarPorId(999);

        verifyNoInteractions(clienteVeiculoRepository);
    }


}
