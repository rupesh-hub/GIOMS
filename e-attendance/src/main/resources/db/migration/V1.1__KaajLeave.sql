drop function if exists kaaj_leave(character varying, character varying,character varying);
create function kaaj_leave(fromdate character varying, todate character varying, office character varying)
    returns TABLE(leavecount bigint, kaajcount bigint)
    language plpgsql
as
$$
declare
        dynamicQuery varchar;
BEGIN
        dynamicQuery :='select * from (select count(distinct da.id)::bigint as leave_count
                                 from decision_approval da
                                          inner join leave_request_detail lrd on da.leave_request_detail_id = lrd.id
                                          inner join leave_request lr on lrd.leave_request_id = lr.id
                                 where lrd.is_active = true
                                   and lrd.status = ''A''
                                   and da.is_active = true
                                   and date(da.last_modified_date) between '''||fromdate||''' and '''||todate||'''
                                   and lr.office_code = any('''||office||''')) as lr,
                                    (select count(distinct da. id):: bigint as kaaj_count
                                   from decision_approval da
                                   inner join kaaj_request kr on da.kaaj_request_id = kr.id
                                   where kr.is_active= true and kr.status=''A'' and da.is_active= true
                                   and date(da.last_modified_date) between '''||fromdate||''' and '''||todate||'''
                                   and kr.office_code =any('''||office||''')) as kr';
        return QUERY execute dynamicQuery;
END;
$$;

alter function kaaj_leave(varchar, varchar, varchar) owner to postgres;

