import "./index.css";
import { StatusBadge } from "./components/StatusBadge";
import { agents, approvals, auditLogs, stats, tasks, tools } from "./data/mock";

export default function App() {
  return (
    <div className="min-h-screen bg-surface">
      <header className="border-b border-sand bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-5">
          <div>
            <p className="text-xs uppercase tracking-[0.3em] text-steel">BizFlow Agent Hub</p>
            <h1 className="font-display text-2xl font-semibold text-ink">Admin Console</h1>
          </div>
          <div className="flex gap-3">
            <button className="rounded-full bg-ink px-4 py-2 text-sm font-semibold text-white">New Task</button>
            <button className="rounded-full border border-ink px-4 py-2 text-sm font-semibold text-ink">Refresh</button>
          </div>
        </div>
      </header>

      <main className="mx-auto grid max-w-6xl gap-6 px-6 py-8">
        <section className="grid gap-4 md:grid-cols-4">
          {stats.map((stat) => (
            <div key={stat.label} className="rounded-2xl bg-white p-5 shadow-sm">
              <p className="text-sm text-steel">{stat.label}</p>
              <p className="mt-3 font-display text-3xl font-semibold text-ink">{stat.value}</p>
            </div>
          ))}
        </section>

        <section className="grid gap-6 lg:grid-cols-2">
          <div className="rounded-2xl bg-white p-6 shadow-sm">
            <h2 className="font-display text-xl font-semibold text-ink">Task List</h2>
            <div className="mt-4 overflow-hidden rounded-xl border border-sand">
              <table className="w-full text-left text-sm">
                <thead className="bg-sand text-xs uppercase tracking-wider text-steel">
                  <tr>
                    <th className="px-4 py-3">Task</th>
                    <th className="px-4 py-3">Type</th>
                    <th className="px-4 py-3">Owner</th>
                    <th className="px-4 py-3">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {tasks.map((task) => (
                    <tr key={task.id} className="border-t border-sand">
                      <td className="px-4 py-3 font-medium text-ink">{task.id}</td>
                      <td className="px-4 py-3 text-steel">{task.type}</td>
                      <td className="px-4 py-3 text-steel">{task.owner}</td>
                      <td className="px-4 py-3">
                        <StatusBadge value={task.status} />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div className="rounded-2xl bg-white p-6 shadow-sm">
            <h2 className="font-display text-xl font-semibold text-ink">Approval Queue</h2>
            <div className="mt-4 space-y-4">
              {approvals.map((approval) => (
                <div key={approval.id} className="rounded-xl border border-sand p-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-steel">{approval.id}</p>
                      <p className="mt-1 font-medium text-ink">{approval.reason}</p>
                      <p className="text-xs text-steel">Run: {approval.run}</p>
                    </div>
                    <StatusBadge value={approval.status} />
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="grid gap-6 lg:grid-cols-2">
          <div className="rounded-2xl bg-white p-6 shadow-sm">
            <h2 className="font-display text-xl font-semibold text-ink">Audit Log Viewer</h2>
            <ul className="mt-4 space-y-3 text-sm">
              {auditLogs.map((log) => (
                <li key={log.id} className="rounded-xl border border-sand p-3">
                  <p className="text-xs text-steel">{log.id} • {log.run}</p>
                  <p className="mt-1 font-medium text-ink">{log.action}</p>
                  <p className="text-sm text-steel">{log.message}</p>
                </li>
              ))}
            </ul>
          </div>

          <div className="rounded-2xl bg-white p-6 shadow-sm">
            <h2 className="font-display text-xl font-semibold text-ink">Workflow Run Detail</h2>
            <div className="mt-4 rounded-xl border border-sand p-4">
              <p className="text-sm text-steel">Run: WR-7781</p>
              <p className="mt-2 font-medium text-ink">Invoice Approval Workflow</p>
              <div className="mt-3 flex flex-wrap gap-2">
                {[
                  "ROUTER_AGENT",
                  "CONTEXT_AGENT",
                  "POLICY_AGENT",
                  "WAITING_APPROVAL",
                ].map((step) => (
                  <span key={step} className="badge bg-sand text-ink">
                    {step}
                  </span>
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="grid gap-6 lg:grid-cols-2">
          <div className="rounded-2xl bg-white p-6 shadow-sm">
            <h2 className="font-display text-xl font-semibold text-ink">Tools Registry</h2>
            <div className="mt-4 space-y-3">
              {tools.map((tool) => (
                <div key={tool.name} className="flex items-center justify-between rounded-xl border border-sand px-4 py-3">
                  <div>
                    <p className="font-medium text-ink">{tool.name}</p>
                    <p className="text-xs text-steel">{tool.level}</p>
                  </div>
                  <StatusBadge value={tool.approval === "YES" ? "WAITING_APPROVAL" : "COMPLETED"} />
                </div>
              ))}
            </div>
          </div>

          <div className="rounded-2xl bg-white p-6 shadow-sm">
            <h2 className="font-display text-xl font-semibold text-ink">Agents Registry</h2>
            <div className="mt-4 space-y-3">
              {agents.map((agent) => (
                <div key={agent.name} className="flex items-center justify-between rounded-xl border border-sand px-4 py-3">
                  <p className="font-medium text-ink">{agent.name}</p>
                  <StatusBadge value={agent.status} />
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="rounded-2xl bg-white p-6 shadow-sm">
          <h2 className="font-display text-xl font-semibold text-ink">Knowledge Docs</h2>
          <p className="mt-2 text-sm text-steel">
            This demo view reads from mock data. Integrate with /api/knowledge/search to list docs.
          </p>
        </section>
      </main>
    </div>
  );
}
