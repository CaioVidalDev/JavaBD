package com.example.javabd.controller;

import com.example.javabd.model.Tarefa;
import com.example.javabd.repository.TarefaRepository;
import com.example.javabd.view.TarefaView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private TarefaRepository tarefaRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Tarefa> tarefas = tarefaRepository.findAll();
        model.addAttribute("tarefas", tarefas);

        // Consulta Aninhada para obter a quantidade de tarefas concluídas e pendentes
        Map<String, Long> tarefasStatus = tarefaRepository.getTaskStatusCount();
        model.addAttribute("tarefasStatus", tarefasStatus);

        // Utilizar a View para obter a lista de tarefas com status
        List<TarefaView> tarefasView = tarefaRepository.getTarefasComStatus();
        model.addAttribute("tarefasView", tarefasView);

        // Calcular a porcentagem de tarefas concluídas usando a Função Simples
        double porcentagemConcluidas = tarefaRepository.getPercentageDoneTasks();
        model.addAttribute("porcentagemConcluidas", porcentagemConcluidas > 0 ? porcentagemConcluidas : 0.0);

        return "home/projeto-tarefas/index";
    }

    @GetMapping("/cadastrar-tarefa")
    public String cadastrarTarefaPage(Model model) {
        model.addAttribute("tarefa", new Tarefa());
        return "home/projeto-tarefas/cadastrar-tarefa";
    }

    @PostMapping("/cadastrar-tarefa")
    public String cadastrarTarefa(@ModelAttribute Tarefa tarefa) {
        if (tarefa != null && tarefa.getTitulo() != null && !tarefa.getTitulo().isEmpty()) {
            tarefaRepository.save(tarefa);
        }
        return "redirect:/";
    }

    @GetMapping("/editar-tarefa/{id}")
    public String preencherFormularioEdicao(@PathVariable("id") Long id, Model model) {
        Tarefa tarefa = tarefaRepository.findById(id).orElse(null);
        if (tarefa != null) {
            model.addAttribute("tarefa", tarefa);
            return "home/projeto-tarefas/editar-tarefa";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/editar-tarefa")
    public String editarTarefa(@ModelAttribute Tarefa tarefa) {
        if (tarefa != null) {
            Tarefa tarefaAnterior = tarefaRepository.findById(tarefa.getId()).orElse(null);
            if (tarefaAnterior != null && tarefaAnterior.getStatus() != null && "PENDENTE".equals(tarefaAnterior.getStatus()) && tarefa.getStatus() != null && "CONCLUÍDA".equals(tarefa.getStatus())) {
                tarefa.setDataConclusao(new Date());
            }
            tarefaRepository.updateTaskStatus(tarefa.getId(), tarefa.getStatus());
            tarefaRepository.save(tarefa);
        }
        return "redirect:/";
    }

    @GetMapping("/excluir-tarefa/{id}")
    public String preencherFormularioExclusao(@PathVariable("id") Long id, Model model) {
        Tarefa tarefa = tarefaRepository.findById(id).orElse(null);
        if (tarefa != null) {
            model.addAttribute("tarefa", tarefa);
            return "home/projeto-tarefas/excluir-tarefa";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/excluir-tarefa")
    public String excluirTarefa(@RequestParam("id") Long id) {
        tarefaRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/visualizar-tarefa/{id}")
    public String preencherFormularioVisualizacao(@PathVariable("id") Long id, Model model) {
        Tarefa tarefa = tarefaRepository.findById(id).orElse(null);
        if (tarefa != null) {
            model.addAttribute("tarefa", tarefa);
            return "home/projeto-tarefas/visualizar-tarefa";
        } else {
            return "redirect:/";
        }
    }
}