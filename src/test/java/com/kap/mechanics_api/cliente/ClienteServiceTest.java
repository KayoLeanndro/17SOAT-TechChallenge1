package com.kap.mechanics_api.cliente;

import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.dto.cliente.*;
import com.kap.mechanics_api.exception.ClienteNaoEncontradoException;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.mapper.ClienteMapper;
import com.kap.mechanics_api.repository.ClienteRepository;
import com.kap.mechanics_api.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveCriarClienteComSucesso(){

        //Arrange
        CriacaoClienteRequestDTO clienteDto = new CriacaoClienteRequestDTO("João Silva", "12345678900", "51999999999", "joao@email.com");
        Cliente cliente = new Cliente("João Silva", "12345678900", "51999999999", "joao@email.com", null);

        Cliente clienteSalvo = new Cliente("João Silva", "12345678900", "51999999999", "joao@email.com", LocalDateTime.now());
        clienteSalvo.setId(1);

        CriacaoClienteResponseDTO clienteSalvoDto = new CriacaoClienteResponseDTO(1, "João Silva", "12345678900", "51999999999", "joao@email.com");

        when(clienteMapper.dtoToEntity(clienteDto)).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(clienteMapper.entityToDto(clienteSalvo)).thenReturn(clienteSalvoDto);

        //act
        CriacaoClienteResponseDTO resultado = clienteService.salvar(clienteDto);

        //Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.nome());
        assertEquals("12345678900", resultado.cpfCnpj());

        assertNotNull(cliente.getDataCriacao());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void deveListarTodosOsClientes(){

        //Arrange
        Cliente clienteSalvo = new Cliente("João Silva", "12345678900", "51999999999", "joao@email.com", LocalDateTime.now());
        clienteSalvo.setId(1);
        Cliente clienteSalvo2 = new Cliente("Maria Souza", "98765432100", "51988888888", "maria@email.com", LocalDateTime.now());
        clienteSalvo2.setId(2);

        List<Cliente> clientes = new ArrayList<>();
        clientes.add(clienteSalvo);
        clientes.add(clienteSalvo2);

        List<ListagemClienteResponseDTO> clientesDto = new ArrayList<>();
        ListagemClienteResponseDTO dto1 = new ListagemClienteResponseDTO(1, "João Silva", "12345678900", "51999999999", "joao@email.com");
        ListagemClienteResponseDTO dto2 = new ListagemClienteResponseDTO(2, "Maria Souza", "98765432100", "51988888888", "maria@email.com");
        clientesDto.add(dto1);
        clientesDto.add(dto2);

        when(clienteRepository.findAll()).thenReturn(clientes);
        when(clienteMapper.listEntityToListDto(clientes)).thenReturn(clientesDto);

        //act
        List<ListagemClienteResponseDTO> resultado = clienteService.listar();

        //Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(1, resultado.get(0).id());
        assertEquals("João Silva", resultado.get(0).nome());
        assertEquals("12345678900", resultado.get(0).cpfCnpj());
        assertEquals("51999999999", resultado.get(0).telefone());
        assertEquals("joao@email.com", resultado.get(0).email());

        assertEquals(2, resultado.get(1).id());
        assertEquals("Maria Souza", resultado.get(1).nome());

        verify(clienteRepository).findAll();
        verify(clienteMapper).listEntityToListDto(clientes);
    }

    @Test
    void deveBuscarClientePorId(){

        //arrange
        Cliente cliente = new Cliente("João Silva", "12345678900", "51999999999", "joao@email.com", LocalDateTime.now());
        cliente.setId(1);
        Optional<Cliente> clienteOptional = Optional.of(cliente);

        ListagemClienteResponseDTO dto1 = new ListagemClienteResponseDTO(1, "João Silva", "12345678900", "51999999999", "joao@email.com");

        when(clienteRepository.findById(1)).thenReturn(clienteOptional);
        when(clienteMapper.entityToListagemDto(cliente)).thenReturn(dto1);

        //act
        ListagemClienteResponseDTO resultado = clienteService.buscarPorId(cliente.getId());

        //assert
        assertNotNull(resultado);
        assertEquals(1, resultado.id());
        assertEquals("João Silva", resultado.nome());
        assertEquals("12345678900", resultado.cpfCnpj());
        assertEquals("51999999999", resultado.telefone());
        assertEquals("joao@email.com", resultado.email());

        verify(clienteRepository).findById(1);
        verify(clienteMapper).entityToListagemDto(cliente);
    }

    @Test
    void deveDeletarClienteComSucesso(){

        //arrange
        Cliente cliente = new Cliente("João Silva", "12345678900", "51999999999", "joao@email.com", LocalDateTime.now());
        cliente.setId(1);
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        //act
        clienteService.deletar(cliente.getId());

        //assert
        verify(clienteRepository).findById(1);
        verify(clienteRepository).delete(cliente);
    }

    @Test
    void deveAtualizarClienteComSucesso(){

        //arrange
        Cliente cliente = new Cliente("João Silva", "12345678900", "51999999999", "joao@email.com", LocalDateTime.now());
        cliente.setId(1);

        AtualizacaoClienteRequestDTO dto = new AtualizacaoClienteRequestDTO("João Silva Junior", "12345678900", "51988887777", "joaojr@email.com");

        Cliente clienteAlterado = new Cliente("João Silva Junior", "12345678900", "51988887777", "joaojr@email.com", cliente.getDataCriacao());
        clienteAlterado.setId(1);

        AtualizacaoClienteResponseDTO dtoResposta = new AtualizacaoClienteResponseDTO(1, "João Silva Junior", "12345678900", "51988887777", "joaojr@email.com");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAlterado);
        when(clienteMapper.entityToAtualizacaoDto(clienteAlterado)).thenReturn(dtoResposta);

        //act
        AtualizacaoClienteResponseDTO resposta = clienteService.atualizar(dto, cliente.getId());

        //assert
        assertNotNull(resposta);
        assertEquals(1, resposta.id());
        assertEquals("João Silva Junior", resposta.nome());
        assertEquals("12345678900", resposta.cpfCnpj());
        assertEquals("51988887777", resposta.telefone());
        assertEquals("joaojr@email.com", resposta.email());

        verify(clienteRepository).findById(1);
        verify(clienteMapper).entityToAtualizacaoDto(clienteAlterado);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void deveRetornarExceptionCasoClienteNaoExista(){

        //Arrange
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        //act
        ClienteNaoEncontradoException exception = assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.buscarPorId(1));

        //assert
        assertEquals("Cliente nao encontrado com o id 1", exception.getMessage());
        verify(clienteRepository).findById(1);
    }

    @Test
    void deveRetornarExceptionQuandoNaoInformadoNenhumParametroParaAtualizacao(){

        //arrange
        AtualizacaoClienteRequestDTO dto = new AtualizacaoClienteRequestDTO(null, null, null, null);

        //act
        NenhumCampoInformadoException exception = assertThrows(
                NenhumCampoInformadoException.class,
                () -> clienteService.atualizar(dto, 1)
        );

        //assertion
        assertEquals("Nenhum campo foi informado para atualização", exception.getMessage());
        verifyNoInteractions(clienteRepository);
    }

}
