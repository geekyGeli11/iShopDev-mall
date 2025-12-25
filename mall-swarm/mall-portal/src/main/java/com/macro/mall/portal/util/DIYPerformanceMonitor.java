package com.macro.mall.portal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DIY性能监控工具
 * Created by macro on 2024/12/20.
 */
@Component
public class DIYPerformanceMonitor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DIYPerformanceMonitor.class);
    
    // 性能统计数据
    private final Map<String, PerformanceStats> performanceStats = new ConcurrentHashMap<>();
    
    // 当前执行的操作
    private final ThreadLocal<OperationContext> currentOperation = new ThreadLocal<>();
    
    /**
     * 开始监控操作
     */
    public void startOperation(String operationName) {
        OperationContext context = new OperationContext(operationName, System.currentTimeMillis());
        currentOperation.set(context);
        LOGGER.debug("开始监控操作: {}", operationName);
    }
    
    /**
     * 结束监控操作
     */
    public void endOperation() {
        OperationContext context = currentOperation.get();
        if (context != null) {
            long duration = System.currentTimeMillis() - context.startTime;
            recordPerformance(context.operationName, duration);
            currentOperation.remove();
            LOGGER.debug("结束监控操作: {}, 耗时: {}ms", context.operationName, duration);
        }
    }
    
    /**
     * 记录操作耗时
     */
    public void recordPerformance(String operationName, long duration) {
        performanceStats.computeIfAbsent(operationName, k -> new PerformanceStats())
                      .recordExecution(duration);
    }
    
    /**
     * 获取性能统计信息
     */
    public PerformanceStats getPerformanceStats(String operationName) {
        return performanceStats.get(operationName);
    }
    
    /**
     * 获取所有性能统计信息
     */
    public Map<String, PerformanceStats> getAllPerformanceStats() {
        return new ConcurrentHashMap<>(performanceStats);
    }
    
    /**
     * 清除性能统计数据
     */
    public void clearStats() {
        performanceStats.clear();
        LOGGER.info("清除所有性能统计数据");
    }
    
    /**
     * 清除指定操作的性能统计数据
     */
    public void clearStats(String operationName) {
        performanceStats.remove(operationName);
        LOGGER.info("清除操作性能统计数据: {}", operationName);
    }
    
    /**
     * 检查是否有慢操作
     */
    public void checkSlowOperations(long thresholdMs) {
        performanceStats.forEach((operationName, stats) -> {
            if (stats.getAverageTime() > thresholdMs) {
                LOGGER.warn("发现慢操作: {}, 平均耗时: {}ms, 执行次数: {}", 
                           operationName, stats.getAverageTime(), stats.getExecutionCount());
            }
        });
    }
    
    /**
     * 生成性能报告
     */
    public String generatePerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== DIY性能监控报告 ===\n");
        
        performanceStats.forEach((operationName, stats) -> {
            report.append(String.format("操作: %s\n", operationName));
            report.append(String.format("  执行次数: %d\n", stats.getExecutionCount()));
            report.append(String.format("  总耗时: %dms\n", stats.getTotalTime()));
            report.append(String.format("  平均耗时: %.2fms\n", stats.getAverageTime()));
            report.append(String.format("  最小耗时: %dms\n", stats.getMinTime()));
            report.append(String.format("  最大耗时: %dms\n", stats.getMaxTime()));
            report.append("\n");
        });
        
        return report.toString();
    }
    
    /**
     * 操作上下文
     */
    private static class OperationContext {
        private final String operationName;
        private final long startTime;
        
        public OperationContext(String operationName, long startTime) {
            this.operationName = operationName;
            this.startTime = startTime;
        }
    }
    
    /**
     * 性能统计数据
     */
    public static class PerformanceStats {
        private final AtomicLong executionCount = new AtomicLong(0);
        private final AtomicLong totalTime = new AtomicLong(0);
        private volatile long minTime = Long.MAX_VALUE;
        private volatile long maxTime = Long.MIN_VALUE;
        
        public void recordExecution(long duration) {
            executionCount.incrementAndGet();
            totalTime.addAndGet(duration);
            
            // 更新最小值
            long currentMin = minTime;
            while (duration < currentMin && !compareAndSetMin(currentMin, duration)) {
                currentMin = minTime;
            }
            
            // 更新最大值
            long currentMax = maxTime;
            while (duration > currentMax && !compareAndSetMax(currentMax, duration)) {
                currentMax = maxTime;
            }
        }
        
        private boolean compareAndSetMin(long expect, long update) {
            synchronized (this) {
                if (minTime == expect) {
                    minTime = update;
                    return true;
                }
                return false;
            }
        }
        
        private boolean compareAndSetMax(long expect, long update) {
            synchronized (this) {
                if (maxTime == expect) {
                    maxTime = update;
                    return true;
                }
                return false;
            }
        }
        
        public long getExecutionCount() {
            return executionCount.get();
        }
        
        public long getTotalTime() {
            return totalTime.get();
        }
        
        public double getAverageTime() {
            long count = executionCount.get();
            return count > 0 ? (double) totalTime.get() / count : 0.0;
        }
        
        public long getMinTime() {
            return minTime == Long.MAX_VALUE ? 0 : minTime;
        }
        
        public long getMaxTime() {
            return maxTime == Long.MIN_VALUE ? 0 : maxTime;
        }
        
        @Override
        public String toString() {
            return String.format("PerformanceStats{count=%d, total=%dms, avg=%.2fms, min=%dms, max=%dms}",
                    getExecutionCount(), getTotalTime(), getAverageTime(), getMinTime(), getMaxTime());
        }
    }
    
    /**
     * 性能监控注解处理器（可以配合AOP使用）
     */
    public static class PerformanceMonitorAspect {
        
        private final DIYPerformanceMonitor monitor;
        
        public PerformanceMonitorAspect(DIYPerformanceMonitor monitor) {
            this.monitor = monitor;
        }
        
        public Object monitorPerformance(String operationName, Runnable operation) {
            monitor.startOperation(operationName);
            try {
                operation.run();
                return null;
            } finally {
                monitor.endOperation();
            }
        }
        
        public <T> T monitorPerformance(String operationName, java.util.function.Supplier<T> operation) {
            monitor.startOperation(operationName);
            try {
                return operation.get();
            } finally {
                monitor.endOperation();
            }
        }
    }
}
