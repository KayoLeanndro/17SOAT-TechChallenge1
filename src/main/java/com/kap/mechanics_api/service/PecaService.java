package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.Peca;
import com.kap.mechanics_api.dto.peca.AtualizacaoPecaRequestDTO;
import com.kap.mechanics_api.dto.peca.CriacaoPecaRequestDTO;
import com.kap.mechanics_api.dto.peca.PecaResponseDTO;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.PecaNaoEncontradaException;
import com.kap.mechanics_api.mapper.PecaMapper;
import com.kap.mechanics_api.repository.PecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PecaService {

    private final PecaRepository pecaRepository;
    private final PecaMapper pecaMapper;

    public PecaService(PecaRepository pecaRepository, PecaMapper pecaMapper) {
        this.pecaRepository = pecaRepository;
        this.pecaMapper = pecaMapper;
    }

    public PecaResponseDTO cadastrar(CriacaoPecaRequestDTO dto) {
        return pecaMapper.toResponseDto(pecaRepository.save(pecaMapper.toEntity(dto)));
    }

    public List<PecaResponseDTO> listar() {
        return pecaMapper.toResponseDtoList(pecaRepository.findAll());
    }

    public PecaResponseDTO buscarPorId(Integer id) {
        return pecaMapper.toResponseDto(pesquisarPorId(id));
    }

    public Peca pesquisarPorId(Integer id) {
        return pecaRepository.findById(id)
                .orElseThrow(() -> new PecaNaoEncontradaException(id));
    }

    public PecaResponseDTO atualizar(Integer id, AtualizacaoPecaRequestDTO dto) {

        if (!dto.temAoMenosUmCampoPreenchido()) {
            throw new NenhumCampoInformadoException(AtualizacaoPecaRequestDTO.class);
        }

        Peca peca = pesquisarPorId(id);
        if (StringUtils.hasText(dto.nome())) peca.setNome(dto.nome());
        if (StringUtils.hasText(dto.descricao())) peca.setDescricao(dto.descricao());
        if (dto.valorUnitario() != null) peca.setValorUnitario(dto.valorUnitario());
        if (dto.quantidadeAtual() != null) peca.setQuantidadeAtual(dto.quantidadeAtual());
        if (dto.quantidadeMinima() != null) peca.setQuantidadeMinima(dto.quantidadeMinima());
        if (dto.ativo() != null) peca.setAtivo(dto.ativo());

        return pecaMapper.toResponseDto(pecaRepository.save(peca));
    }

    public void deletar(Integer id) {
        pecaRepository.delete(pesquisarPorId(id));
    }
}
