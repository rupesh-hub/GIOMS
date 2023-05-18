CREATE
OR REPLACE FUNCTION public.tippani_reference_list(v_id bigint)
 RETURNS TABLE(value bigint)
 LANGUAGE plpgsql
AS $function$
declare
dynamicQuery varchar;
BEGIN
    dynamicQuery :='select tt.referenced_memo_id from
       (select * from (
       WITH RECURSIVE a AS (
        SELECT mr.referenced_memo_id, mr.memo_id
        FROM memo_reference mr
        WHERE mr.memo_id  = '''||v_id||''' and mr.is_active = true and mr.referenced_memo_id is not null and mr.is_attach = false
        UNION ALL
        SELECT d.referenced_memo_id,d.memo_id
        FROM memo_reference d
            INNER JOIN a ON a.referenced_memo_id  = d.memo_id where d.referenced_memo_id is not null and d.is_attach = false
        )select * from a
        ) t
        UNION ALL
        select * from(
        WITH RECURSIVE a AS (
        SELECT mr.memo_id  as referenced_memo_id ,mr.memo_id
        FROM memo_reference mr
        WHERE mr.referenced_memo_id = '''||v_id||''' and mr.is_active = true and mr.memo_id is not null and mr.is_attach = false
        UNION ALL
        SELECT d.memo_id  as referenced_memo_id,d.memo_id
        FROM memo_reference d
            INNER JOIN a ON a.memo_id = d.referenced_memo_id where d.memo_id is not null and d.is_attach = false
        )select * from a)
        t
        union all
        SELECT '''||v_id||''' as referenced_memo_id , '''||v_id||''' as memo_id
        )tt order by tt.referenced_memo_id
';

    return QUERY execute dynamicQuery;

END;
$function$
;