# Tool Hub

## Tool Contract
Each tool is registered with:
- `tool_name`
- `version`
- `description`
- `input_schema` / `output_schema`
- `side_effect_level` (READ/WRITE/DESTRUCTIVE)
- `approval_required`

## Policy Gate
- WRITE/DESTRUCTIVE tools can require approval.
- Tool Hub enforces tenant-level and role checks.
- All tool calls are logged to `tool_calls` and `audit_logs`.

## Schema Design
Tool definitions live in `tools` table and are joined to `tool_calls` for observability.

## Registration Example
```json
{
  "tool_name": "create_ticket",
  "version": "v1",
  "description": "Create support ticket",
  "input_schema": {"type": "object"},
  "output_schema": {"type": "object"},
  "side_effect_level": "WRITE",
  "approval_required": false
}
```
