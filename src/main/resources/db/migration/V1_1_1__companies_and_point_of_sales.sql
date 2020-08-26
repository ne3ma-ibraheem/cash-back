
CREATE TABLE COMPANIES(

                          id uuid not null primary key,
                          name         varchar(250) not null,
                          owner_id     varchar(250) not null,
                          display_name varchar(250),
                          address      varchar(250),
                          website      varchar(250),
                          description  varchar(1000),
                          picture_url  varchar(1000),
                          constraint fk_companies_owner
                              foreign key(owner_id) references USERS(username)
                                  on delete cascade

);


CREATE TABLE POINT_OF_SALES (
                                id uuid not null primary key,
                                name       varchar(250)     not null,
                                address    varchar(250)     not null,
                                longitude  double precision not null,
                                latitude   double precision not null,
                                company_id uuid             not null,
                                constraint fk_pos_company
                                    foreign key(company_id) references companies(id)
                                        on delete cascade
);