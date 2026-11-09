-- V1: Criação das tabelas principais do Analisador de Editais de IC

CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    keywords TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS opportunities (
    id UUID PRIMARY KEY,
    source_url VARCHAR(2048) NOT NULL UNIQUE,
    source_name VARCHAR(255) NOT NULL,
    title VARCHAR(500) NOT NULL,
    institution VARCHAR(255),
    published_at TIMESTAMP WITH TIME ZONE,
    deadline_at TIMESTAMP WITH TIME ZONE,
    raw_content TEXT NOT NULL,
    content_hash VARCHAR(64) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_opportunities_status ON opportunities(status);
CREATE INDEX IF NOT EXISTS idx_opportunities_deadline ON opportunities(deadline_at);
CREATE INDEX IF NOT EXISTS idx_opportunities_content_hash ON opportunities(content_hash);

CREATE TABLE IF NOT EXISTS analyses (
    id UUID PRIMARY KEY,
    opportunity_id UUID NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    summary TEXT NOT NULL,
    match_score NUMERIC(5, 2) NOT NULL,
    classification VARCHAR(50) NOT NULL,
    requirements TEXT,
    risks TEXT,
    model_name VARCHAR(100) NOT NULL,
    prompt_version VARCHAR(50) NOT NULL,
    analyzed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_opportunity_profile UNIQUE (opportunity_id, profile_id)
);

CREATE INDEX IF NOT EXISTS idx_analyses_classification ON analyses(classification);
CREATE INDEX IF NOT EXISTS idx_analyses_profile ON analyses(profile_id);

CREATE TABLE IF NOT EXISTS jobs (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    total_urls INT NOT NULL DEFAULT 0,
    processed_urls INT NOT NULL DEFAULT 0,
    failed_urls INT NOT NULL DEFAULT 0,
    error_details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
