import React from "react";

const colors: Record<string, string> = {
  COMPLETED: "bg-moss text-white",
  QUEUED: "bg-steel text-white",
  WAITING_APPROVAL: "bg-accent text-white",
  PENDING: "bg-accent text-white",
  ONLINE: "bg-moss text-white",
};

export function StatusBadge({ value }: { value: string }) {
  const classes = colors[value] || "bg-sand text-ink";
  return <span className={`badge ${classes}`}>{value}</span>;
}
