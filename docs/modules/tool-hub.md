# Tool Hub Module

## Muc tieu
- Quan ly registry tool, validate input va ghi lai lich su goi tool theo workflow run.
- Tach biet tool read/write va approval gate theo cach nhin phu hop cho MVP enterprise.

## Nang cap da hoan thien
- Bo sung validation bat buoc theo tung tool thay vi cho phep payload bat ky.
- Output mock duoc chuyen tu `echo` chung sang ket qua co y nghia theo tool.
- Them `GET /api/tools/calls/{workflowRunId}` de xem lich su goi tool.
- `ToolInvokeResponse` bo sung `callId` va `sideEffectLevel`.
- Context test duoc thay bang service test cho invalid input, approval flow va call history.

## API chinh
### `GET /api/tools`
- Liet ke tool registry da seed trong he thong.

### `GET /api/tools/calls/{workflowRunId}`
- Liet ke cac tool call theo workflow run kem request/response payload.

### `POST /api/tools/{toolName}/invoke`
```json
{
  "workflowRunId": "run-123",
  "input": {
    "query": "invoice"
  }
}
```

## Luu y thiet ke
- `submit_approval_request` khong thuc thi tac vu ngay lap tuc ma tra ve `WAITING_APPROVAL`.
- Validation hien tai la rule-based, de de nang cap thanh schema-based validator ve sau.
- Lich su tool call duoc map kem metadata cua registry de UI khong can tu join du lieu.
