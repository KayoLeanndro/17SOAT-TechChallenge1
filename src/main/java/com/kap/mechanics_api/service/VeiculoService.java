package com.kap.mechanics_api.service;


import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.dto.*;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.VeiculoNaoEncontradoException;
import com.kap.mechanics_api.mapper.VeiculoMapper;
import com.kap.mechanics_api.repository.VeiculoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final VeiculoMapper veiculoMapper;

    public VeiculoService(VeiculoRepository veiculoRepository, VeiculoMapper mapper){
        this.veiculoRepository = veiculoRepository;
        this.veiculoMapper = mapper;
    }

    public CriacaoVeiculoResponseDTO cadastrar(@Valid CriacaoVeiculoRequestDTO dto) {

        Veiculo veiculo = veiculoMapper.toEntity(dto);
        veiculo.setDataCriacao(LocalDateTime.now());
        veiculo = veiculoRepository.save(veiculo);
        return veiculoMapper.toResponseDto(veiculo);
    }

    public List<ListagemVeiculoResponseDTO> listar(){
        List<Veiculo> veiculos = veiculoRepository.findAll();
        return veiculoMapper.toListagemDto(veiculos);
    }

    public Veiculo pesquisarPorId(Integer id){
        return veiculoRepository.findById(id).orElseThrow( () -> new VeiculoNaoEncontradoException(id));
    }

    public ListagemVeiculoResponseDTO buscarPorId(Integer id){
        Veiculo veiculo = pesquisarPorId(id);
        return veiculoMapper.toListagemVeiculoResponseDto(veiculo);
    }

    public void deletar(Integer id) {
        Veiculo veiculo = pesquisarPorId(id);
        veiculoRepository.delete(veiculo);
    }

    public AtualizacaoVeiculoResponseDTO atualizar(AtualizacaoVeiculoRequestDTO dto, Integer id){

        if(!dto.temAoMenosUmCampoPreenchido()){
            throw new NenhumCampoInformadoException(AtualizacaoVeiculoRequestDTO.class);
        }

        Veiculo veiculo = pesquisarPorId(id);
        if(dto.ano() != null){
            veiculo.setAno(dto.ano());
        }

        if(StringUtils.hasText(dto.placa())){
            veiculo.setPlaca(dto.placa());
        }

        if(StringUtils.hasText(dto.marca())){
            veiculo.setModelo(dto.marca());
        }

        Veiculo veiculoAlterado = veiculoRepository.save(veiculo);
        return veiculoMapper.toAtualizacaoVeiculoResponseDto(veiculoAlterado);
    }
}
