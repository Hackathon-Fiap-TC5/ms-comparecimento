-- ============================================
-- Database Creation Script for ms-comparecimento
-- CloudSQL MySQL Database
-- ============================================

-- Create database (if it doesn't exist)
CREATE DATABASE IF NOT EXISTS `feedback` 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE `feedback`;

-- ============================================
-- Table: tb_paciente
-- Stores patient attendance and absence data
-- ============================================
CREATE TABLE IF NOT EXISTS `tb_paciente` (
    `cns` VARCHAR(15) NOT NULL COMMENT 'Cartão Nacional de Saúde - Primary Key',
    `icc` INT DEFAULT NULL COMMENT 'Índice de Comparecimento do Cliente',
    `classificacao` VARCHAR(50) DEFAULT NULL COMMENT 'Classificação do paciente',
    `total_comparecimentos` INT DEFAULT 0 COMMENT 'Total de comparecimentos',
    `total_faltas` INT DEFAULT 0 COMMENT 'Total de faltas',
    `total_confirmacoes` INT DEFAULT 0 COMMENT 'Total de confirmações',
    `total_agendamentos` INT DEFAULT 0 COMMENT 'Total de agendamentos',
    `ultima_atualizacao` TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Data e hora da última atualização',
    PRIMARY KEY (`cns`),
    INDEX `idx_ultima_atualizacao` (`ultima_atualizacao`),
    INDEX `idx_classificacao` (`classificacao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Tabela de pacientes com índices de comparecimento';

-- ============================================
-- Initial Data (Optional - for testing)
-- ============================================
-- Uncomment below if you want to insert sample data for testing

-- INSERT INTO `tb_paciente` (`cns`, `icc`, `classificacao`, `total_comparecimentos`, `total_faltas`, `total_confirmacoes`, `total_agendamentos`, `ultima_atualizacao`)
-- VALUES
--     ('123456789012345', 85, 'REGULAR', 17, 3, 15, 20, NOW()),
--     ('987654321098765', 90, 'BOM', 18, 2, 18, 20, NOW()),
--     ('111222333444555', 60, 'IRREGULAR', 12, 8, 10, 20, NOW())
-- ON DUPLICATE KEY UPDATE `ultima_atualizacao` = VALUES(`ultima_atualizacao`);
