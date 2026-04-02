# Audit Service Module

## Muc tieu
- Luu vet cac su kien nghiep vu quan trong de phuc vu audit trail va troubleshooting.
- Tieu thu domain event tu Kafka de tao audit trail khong dong bo.

## Nang cap da hoan thien
- Them `POST /api/audit` de append audit log reactive.
- Them `GET /api/audit` ho tro filter theo `workflowRunId` va `action`.
- Giu lai `GET /api/audit/{runId}` de backward-compatible cho truy van theo run.
- Them Kafka consumer worker nghe topic `events` va ghi audit log tu event nhan duoc.
- Them co che idempotent qua `source_event_id` de tranh duplicate khi consumer retry.
- Context test duoc thay bang service test cho append va filter.

## API chinh
### `POST /api/audit`
```json
{
  "workflowRunId": "run-1",
  "action": "TOOL_INVOKED",
  "payload": "{\"tool\":\"read_policy_docs\"}"
}
```

### `GET /api/audit?workflowRunId=run-1&action=TOOL_INVOKED`
- Tra ve audit log da duoc sap xep moi nhat truoc.

## Luu y thiet ke
- Audit log hien tai luu payload dang text/json string de de ghi nhanh va linh hoat cho MVP.
- Event Kafka duoc serialize nguyen envelope vao `payload` de de tra cuu va replay.
- Consumer dung `source_event_id` + unique index de dam bao xu ly lap lai van an toan.
- API moi cho phep ca append chu dong va doc theo bo loc don gian, hop cho admin console va runbook.
