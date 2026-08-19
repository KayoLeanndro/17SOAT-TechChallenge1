package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.OrdemServico;
//import com.kap.mechanics_api.dto.ordemservico.AtualizacaoOrdemServicoRequestDTO;
//import com.kap.mechanics_api.dto.ordemservico.CriacaoOrdemServicoRequestDTO;
//import com.kap.mechanics_api.dto.ordemservico.OrdemServicoResponseDTO;
//import com.kap.mechanics_api.enums.StatusOrdemServico;
//import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
//import com.kap.mechanics_api.exception.OrcamentoNaoEncontradoException;
//import com.kap.mechanics_api.exception.OrdemServicoNaoEncontradaException;
//import com.kap.mechanics_api.mapper.OrdemServicoMapper;
//import com.kap.mechanics_api.repository.OrcamentoRepository;
//import com.kap.mechanics_api.repository.OrdemServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdemServicoService {

//    private final OrdemServicoRepository ordemServicoRepository;
//    private final OrcamentoRepository orcamentoRepository;
//    private final UsuarioService usuarioService;
//    private final OrdemServicoMapper ordemServicoMapper;
//
//    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
//                               OrcamentoRepository orcamentoRepository,
//                               UsuarioService usuarioService,
//                               OrdemServicoMapper ordemServicoMapper) {
//        this.ordemServicoRepository = ordemServicoRepository;
//        this.orcamentoRepository = orcamentoRepository;
//        this.usuarioService = usuarioService;
//        this.ordemServicoMapper = ordemServicoMapper;
//    }
//
//    public OrdemServicoResponseDTO cadastrar(CriacaoOrdemServicoRequestDTO dto) {
//        OrdemServico ordemServico = ordemServicoMapper.toEntity(dto);
//        ordemServico.setOrcamento(orcamentoRepository.findById(dto.orcamentoId()).orElseThrow(() -> new OrcamentoNaoEncontradoException(
//                        "Orçamento não encontrado com o id " + dto.orcamentoId())));
//        ordemServico.setUsuarioAtendente(usuarioService.buscarPorId(dto.usuarioAtendenteId()));
//        ordemServico.setStatus(StatusOrdemServico.RECEBIDA);
//
//        return ordemServicoMapper.toResponseDto(ordemServicoRepository.save(ordemServico));
//    }
//
//    public List<OrdemServicoResponseDTO> listar() {
//        return ordemServicoMapper.toResponseDtoList(ordemServicoRepository.findAll());
//    }
//
//    public OrdemServico pesquisarPorId(Integer id) {
//        return ordemServicoRepository.findById(id)
//                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(id));
//    }
//
//    public OrdemServicoResponseDTO buscarPorId(Integer id) {
//        return ordemServicoMapper.toResponseDto(pesquisarPorId(id));
//    }
//
//    public OrdemServicoResponseDTO atualizar(Integer id, AtualizacaoOrdemServicoRequestDTO dto) {
//        if (!dto.temAoMenosUmCampoPreenchido()) {
//            throw new NenhumCampoInformadoException(AtualizacaoOrdemServicoRequestDTO.class);
//        }
//
//        OrdemServico ordemServico = pesquisarPorId(id);
//        if (dto.usuarioAtendenteId() != null) {
//            ordemServico.setUsuarioAtendente(usuarioService.buscarPorId(dto.usuarioAtendenteId()));
//        }
//        if (dto.statusId() != null) {
//            ordemServico.setStatus(StatusOrdemServico.EM_EXECUCAO);
//        }
//        if (dto.dataEntrega() != null) {
//            ordemServico.setDataEntrega(dto.dataEntrega());
//        }
//
//        return ordemServicoMapper.toResponseDto(ordemServicoRepository.save(ordemServico));
//    }
//
//    public void deletar(Integer id) {
//        ordemServicoRepository.delete(pesquisarPorId(id));
//    }
}
