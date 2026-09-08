using System;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Collections.Generic;

public class Program
{
    public static void Main()
    {
        string path = @"D:\nro zin zin\NRO ZINZIN2025 V3\NRO ZINZIN\database\nro.sql";
        byte[] bytes = File.ReadAllBytes(path);
        string f = Encoding.UTF8.GetString(bytes);
        var lines = f.Split('\n');

        var tableStartLines = new Dictionary<string, int>();
        var tableEndLines = new Dictionary<string, int>();
        for (int i = 0; i < lines.Length; i++)
        {
            var m = Regex.Match(lines[i], @"^CREATE TABLE `(\w+)`");
            if (m.Success) tableStartLines[m.Groups[1].Value] = i;
        }
        var sorted = tableStartLines.OrderBy(p => p.Value).ToList();
        for (int i = 0; i < sorted.Count; i++)
        {
            int endLine = lines.Length;
            if (i + 1 < sorted.Count)
            {
                endLine = sorted[i + 1].Value;
                while (endLine > 0 && (lines[endLine].StartsWith("--") || string.IsNullOrWhiteSpace(lines[endLine])))
                    endLine--;
            }
            tableEndLines[sorted[i].Key] = endLine;
        }

        AnalyzeSkill(lines, tableStartLines["skill_template"], tableEndLines["skill_template"]);
        AnalyzeFirstCol(lines, "item_template", tableStartLines["item_template"], tableEndLines["item_template"]);
        AnalyzeFirstCol(lines, "item_option_template", tableStartLines["item_option_template"], tableEndLines["item_option_template"]);
        AnalyzeFirstCol(lines, "mob_template", tableStartLines["mob_template"], tableEndLines["mob_template"]);
        AnalyzeFirstCol(lines, "npc_template", tableStartLines["npc_template"], tableEndLines["npc_template"]);
        AnalyzeFirstCol(lines, "head_avatar", tableStartLines["head_avatar"], tableEndLines["head_avatar"]);
        AnalyzeFirstCol(lines, "part", tableStartLines["part"], tableEndLines["part"]);
        AnalyzeFirstCol(lines, "bg_item_template", tableStartLines["bg_item_template"], tableEndLines["bg_item_template"]);
    }

    static void AnalyzeFirstCol(string[] lines, string name, int start, int end)
    {
        var ids = new HashSet<int>();
        for (int i = start; i < end; i++)
        {
            var m = Regex.Match(lines[i].TrimStart(), @"^\((\d+)");
            if (m.Success) ids.Add(int.Parse(m.Groups[1].Value));
        }
        if (ids.Count == 0) { Console.WriteLine(name + ": empty"); return; }
        Console.WriteLine(name + ": " + ids.Count + " rows, " + ids.Min() + "-" + ids.Max());
    }

    static void AnalyzeSkill(string[] lines, int start, int end)
    {
        var ids = new HashSet<int>();
        var names = new Dictionary<int, string>();
        for (int i = start; i < end; i++)
        {
            var line = lines[i].Trim().TrimStart('\r');
            if (line.Length < 5 || line[0] != '(') continue;
            // Manual parse: (N,N,'NAME',
            var parts = new List<string>();
            int pi = 1; // skip '('
            bool inStr = false;
            int start_p = pi;
            for (; pi < line.Length; pi++)
            {
                char c = line[pi];
                if (c == '\'' && (pi == 0 || line[pi-1] != '\\'))
                    inStr = !inStr;
                else if (c == ',' && !inStr)
                {
                    parts.Add(line.Substring(start_p, pi - start_p).Trim().Trim('\'').TrimStart('\\'));
                    start_p = pi + 1;
                }
                if (parts.Count >= 2) break;
            }
            if (parts.Count >= 2)
            {
                int id;
                if (int.TryParse(parts[1], out id))
                {
                    ids.Add(id);
                    names[id] = parts.Count > 2 ? parts[2] : "";
                }
            }
        }
        Console.WriteLine("\n=== skill_template (col id) ===");
        Console.WriteLine("Rows parsed: " + ids.Count);
        if (ids.Count == 0) { return; }
        Console.WriteLine("Range: " + ids.Min() + "-" + ids.Max());
        foreach (var id in ids.OrderBy(x => x))
            Console.WriteLine("  " + id + ": " + names[id]);
    }
}
