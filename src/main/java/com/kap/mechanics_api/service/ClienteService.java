package com.kap.mechanics_api.service;

import java.time.LocalDateTime;
import java.util.List;

import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.mapper.ClienteMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kap.mechanics_api.dto.cliente.AtualizacaoClienteRequestDTO;
import com.kap.mechanics_api.dto.cliente.AtualizacaoClienteResponseDTO;
import com.kap.mechanics_api.dto.cliente.CriacaoClienteRequestDTO;
import com.kap.mechanics_api.dto.cliente.CriacaoClienteResponseDTO;
import com.kap.mechanics_api.dto.cliente.ListagemClienteResponseDTO;
import com.kap.mechanics_api.exception.ClienteNaoEncontradoException;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.repository.ClienteRepository;

@Service
public class ClienteService {
	
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    
    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public CriacaoClienteResponseDTO salvar(CriacaoClienteRequestDTO clienteDTO) {

        Cliente cliente = clienteMapper.dtoToEntity(clienteDTO);
        cliente.setDataCriacao(LocalDateTime.now());
        cliente = clienteRepository.save(cliente);
    	return clienteMapper.entityToDto(cliente);
    }
    
    public List<ListagemClienteResponseDTO> listar(){
        List<Cliente> cliente = clienteRepository.findAll();
        return clienteMapper.listEntityToListDto(cliente);
    }
    
    public Cliente pesquisarPorId(Integer id){
        return clienteRepository.findById(id).orElseThrow( () -> new ClienteNaoEncontradoException(id));
    }

    public ListagemClienteResponseDTO buscarPorId(Integer id){
        Cliente cliente = pesquisarPorId(id);
        return clienteMapper.entityToListagemDto(cliente);
    }
    
    public void deletar(Integer id) {
        Cliente cliente = pesquisarPorId(id);
    	clienteRepository.delete(cliente);
    }
    
    public AtualizacaoClienteResponseDTO atualizar(AtualizacaoClienteRequestDTO dto, Integer id){

        if(!dto.temAoMenosUmCampoPreenchido()){
            throw new NenhumCampoInformadoException(AtualizacaoClienteRequestDTO.class);
        }

        Cliente cliente = pesquisarPorId(id);
        
        if(StringUtils.hasText(dto.nome())){
        	cliente.setNome(dto.nome());
        }
        
        if(StringUtils.hasText(dto.cpfCnpj())){
        	cliente.setCpfCnpj(dto.cpfCnpj());
        }

        if(StringUtils.hasText(dto.email())){
        	cliente.setEmail(dto.email());
        }

        if(StringUtils.hasText(dto.telefone())){
        	cliente.setTelefone(dto.telefone());
        }

        Cliente clienteAlterado = clienteRepository.save(cliente);
        return clienteMapper.entityToAtualizacaoDto(clienteAlterado);
    }
    
}
