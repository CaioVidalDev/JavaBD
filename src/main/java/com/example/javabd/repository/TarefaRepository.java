package com.example.javabd.repository;

import com.example.javabd.model.Tarefa;
import com.example.javabd.view.TarefaView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    @Query(value = "SELECT " +
            "  (SELECT COUNT(*) FROM Tarefa t WHERE t.status = 'CONCLUÍDA') AS concluidas, " +
            "  (SELECT COUNT(*) FROM Tarefa t WHERE t.status = 'PENDENTE') AS pendentes " +
            "FROM Tarefa LIMIT 1", nativeQuery = true)
    Map<String, Long> getTaskStatusCount();

    @Query("SELECT " +
            "  t.id, " +
            "  t.titulo, " +
            "  CASE WHEN t.status = 'CONCLUÍDA' THEN 'Concluída' ELSE 'Pendente' END AS status " +
            "FROM Tarefa t")
    List<TarefaView> getTarefasComStatus();

    @Query("SELECT ROUND(100.0 * (SELECT COUNT(*) FROM Tarefa WHERE status = 'CONCLUÍDA') / (SELECT COUNT(*) FROM Tarefa), 2) AS percentageDone")
    double getPercentageDoneTasks();

    @Modifying
    @Transactional
    @Query("UPDATE Tarefa SET status = :status WHERE id = :id")
    void updateTaskStatus(Long id, String status);
}
