drop table vacancies;

ALTER TABLE vacancies ALTER COLUMN description TYPE TEXT;
ALTER TABLE vacancies ALTER COLUMN requirements TYPE TEXT;

drop table history;

CREATE TABLE history (
                         id SERIAL PRIMARY KEY,
                         vacancy_id VARCHAR(255) NOT NULL,
                         type VARCHAR(255),
                         timestamp TIMESTAMP
);