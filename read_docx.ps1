$word = New-Object -ComObject Word.Application
$word.Visible = $false
$doc = $word.Documents.Open('c:\Users\carlo\OneDrive\Escritorio\UNIAJC\PI SEMESTRE 3\PI3.docx')
$paragraphs = $doc.Paragraphs
for ($i = 1; $i -le $paragraphs.Count; $i++) {
    $text = $paragraphs.Item($i).Range.Text.Trim()
    if ($text -ne '') {
        Write-Host "[$i] $text"
    }
}
$doc.Close([ref]$false)
$word.Quit()
