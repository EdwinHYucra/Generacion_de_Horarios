package pe.edu.utp.generador_horario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Limita la generación automática para que no bloquee las peticiones web ni
 * ejecute varios cálculos pesados al mismo tiempo.
 */
@Configuration
public class HorarioAsyncConfig {

    @Bean(name = "horarioTaskExecutor")
    public TaskExecutor horarioTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("horario-generator-");
        executor.initialize();
        return executor;
    }
}
