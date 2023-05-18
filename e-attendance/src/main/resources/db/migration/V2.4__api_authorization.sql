DROP function if exists insert_api_module(varchar, varchar, varchar, varchar, boolean);
create function insert_api_module(apiValue varchar, moduleKey varchar, privilege varchar, apiMethod varchar,
                                  isActiveData bool)
    returns varchar
    language plpgsql as
$$
declare
begin
    if not exists(select 1 from module m where module_key = moduleKey)
    then
        RAISE EXCEPTION 'Invalid Key For Module: %', moduleKey;
    end if;
    if not exists(select 1 from privilege p where p.privilege_key = privilege)
    then
        RAISE EXCEPTION 'Invalid Key For Privilege: %', privilege;
    end if;

    if not exists(select 1
                  from module_api_mapping map2
                           inner join module m2 on map2.module_id = m2.id
                           inner join privilege p2 on map2.privilege_id = p2.id
                  where map2.api = apiValue
                    and m2.module_key = moduleKey
                    and p2.privilege_key = privilege
                    and map2.method = apiMethod)
    then
        begin

            insert into module_api_mapping(id,
                                           created_date,
                                           last_modified_date,
                                           api,
                                           method,
                                           module_id,
                                           privilege_id, is_active)
            values (nextval('module_api_mapping_seq'),
                    current_date, current_date,
                    apiValue,
                    apiMethod,
                    (select m.id from module m where module_key = moduleKey limit 1),
                    (select p.id from privilege p where p.privilege_key = privilege limit 1),
                    true);

            return concat('Successfully Inserted API: ', apiValue, ' in module ', moduleKey,' With Privilege', privilege);
        end;
    else
            update module_api_mapping
            set is_active = isActiveData
            from module_api_mapping map2
                     inner join module m2 on map2.module_id = m2.id
                     inner join privilege p2 on map2.privilege_id = p2.id
            where map2.api = apiValue
              and m2.module_key = moduleKey
              and p2.privilege_key = privilege
              and map2.method = apiMethod;
              return concat('Successfully Updated API: ', apiValue, ' in module ', moduleKey, ' With Privilege', privilege);
    end if;
--                   return concat('Successfully Updated API: ', apiValue, ' in module ', moduleKey, ' With Privilege', privilege);

END
$$;