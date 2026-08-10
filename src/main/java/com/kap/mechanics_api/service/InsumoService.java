package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.Insumo;
import com.kap.mechanics_api.dto.insumo.AtualizacaoInsumoRequestDTO;
import com.kap.mechanics_api.dto.insumo.CriacaoInsumoRequestDTO;
import com.kap.mechanics_api.dto.insumo.InsumoResponseDTO;
import com.kap.mechanics_api.exception.InsumoNaoEncontradoException;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.mapper.InsumoMapper;
import com.kap.mechanics_api.repository.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class InsumoService {
    private final InsumoRepository insumoRepository;
    private final InsumoMapper insumoMapper;

    public InsumoService(InsumoRepository insumoRepository, InsumoMapper insumoMapper) {
        this.insumoRepository = insumoRepository;
        this.insumoMapper = insumoMapper;
    }

    public InsumoResponseDTO cadastrar(CriacaoInsumoRequestDTO dto) {
        return insumoMapper.toResponseDto(insumoRepository.save(insumoMapper.toEntity(dto)));
    }

    public List<InsumoResponseDTO> listar() {
        return insumoMapper.toResponseDtoList(insumoRepository.findAll());
    }

    public InsumoResponseDTO buscarPorId(Integer id) {
        return insumoMapper.toResponseDto(pesquisarPorId(id));
    }

    public Insumo pesquisarPorId(Integer id) {
        return insumoRepository.findById(id)
                .orElseThrow(() -> new InsumoNaoEncontradoException(id));
    }

    public InsumoResponseDTO atualizar(Integer id, AtualizacaoInsumoRequestDTO dto) {
        if (!dto.temAoMenosUmCampoPreenchido()) {
            throw new NenhumCampoInformadoException(AtualizacaoInsumoRequestDTO.class);
        }
        Insumo insumo = pesquisarPorId(id);
        if (StringUtils.hasText(dto.nome())) insumo.setNome(dto.nome());
        if (StringUtils.hasText(dto.descricao())) insumo.setDescricao(dto.descricao());
        if (dto.valorUnitario() != null) insumo.setValorUnitario(dto.valorUnitario());
        if (dto.quantidadeAtual() != null) insumo.setQuantidadeAtual(dto.quantidadeAtual());
        if (dto.quantidadeMinima() != null) insumo.setQuantidadeMinima(dto.quantidadeMinima());
        if (dto.ativo() != null) insumo.setAtivo(dto.ativo());
        return insumoMapper.toResponseDto(insumoRepository.save(insumo));
    }

    public void deletar(Integer id) {
        insumoRepository.delete(pesquisarPorId(id));
    }
}
